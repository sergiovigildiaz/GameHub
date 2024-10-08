    package com.example.demo.controller;

    import com.example.demo.dto.UsuarioLoginDTO;
    import com.example.demo.dto.UsuarioRegistroDTO;
    import com.example.demo.model.SolicitudAmistad;
    import com.example.demo.model.Usuario;
    import com.example.demo.repository.SolicitudAmistadRepository;
    import com.example.demo.repository.UsuarioRepository;
    import com.example.demo.service.SolicitudAmistadService;
    import com.example.demo.service.UsuarioService;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.security.Principal;
    import java.util.List;
    import java.util.Optional;
    import java.util.Set;

    @Controller
    @CrossOrigin(origins = "http://localhost:9090")
    public class UsuarioController {

        @Autowired
        private UsuarioService usuarioService;

        @Autowired
        private SolicitudAmistadRepository solicitudAmistadRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @GetMapping("/registro")
        public String mostrarFormularioRegistro(Model model) {
            model.addAttribute("usuarioRegistroDTO", new UsuarioRegistroDTO());
            return "registro";
        }

        @PostMapping("/registro")
        public String registrarUsuario(@ModelAttribute UsuarioRegistroDTO usuarioRegistroDTO) {
            usuarioService.registrarNuevoUsuario(usuarioRegistroDTO);
            return "redirect:/login";
        }

        @GetMapping("/login")
        public String mostrarFormularioLogin(Model model) {
            model.addAttribute("usuarioLoginDTO", new UsuarioLoginDTO());
            return "login";
        }

        @PostMapping("/login")
        public String loginPost(HttpServletRequest request, @ModelAttribute UsuarioLoginDTO usuarioLoginDTO, Model model) {
            try {
                Usuario usuario = usuarioService.login(usuarioLoginDTO); // Verificar las credenciales del usuario
                HttpSession session = request.getSession();
                session.setAttribute("authenticated", true); // Establecer la autenticación en la sesión
                session.setAttribute("urlFotoPerfil", usuario.getFotoPerfil()); // Establecer la foto de perfil
                session.setAttribute("nombreUsuario", usuario.getNombre()); // Guardar el nombre del usuario en la sesión
                session.setAttribute("usuarioId", usuario.getId());
                return "redirect:/"; // Redirigir a la página de inicio después del inicio de sesión exitoso
            } catch (RuntimeException e) {
                model.addAttribute("error", "Credenciales inválidas"); // Agregar un mensaje de error al modelo
                return "login"; // Volver a cargar la página de inicio de sesión con un mensaje de error
            }
        }

        @GetMapping("/amigos")
        public String mostrarAmigos(Authentication authentication, Model model) {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email);
            Set<Usuario> amigos = usuario.getAmigos();
            List<SolicitudAmistad> solicitudesEnviadas = solicitudAmistadRepository.findByRemitenteAndEstado(usuario, SolicitudAmistad.EstadoSolicitud.PENDIENTE);
            List<SolicitudAmistad> solicitudesRecibidas = solicitudAmistadRepository.findByDestinatarioAndEstado(usuario, SolicitudAmistad.EstadoSolicitud.PENDIENTE);

            model.addAttribute("amigos", amigos);
            model.addAttribute("solicitudesEnviadas", solicitudesEnviadas);
            model.addAttribute("solicitudesRecibidas", solicitudesRecibidas);

            return "amigos";
        }

        @GetMapping("/solicitudesAmistad")
        public String verSolicitudesAmistad(Model model, Principal principal) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("solicitudesRecibidas", usuario.getSolicitudesAmistadRecibidas());
            return "solicitudesAmistad";
        }

        @PostMapping("/enviarSolicitudAmistad")
        public String enviarSolicitudAmistad(@RequestParam String email, Principal principal, Model model) {
            try {
                usuarioService.enviarSolicitudAmistad(principal.getName(), email);
                model.addAttribute("message", "Solicitud de amistad enviada con éxito.");
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }
            return "redirect:/amigos";
        }

        @PostMapping("/aceptarSolicitud")
        @ResponseBody
        public String aceptarSolicitud(Authentication authentication, @RequestParam("solicitudId") Long solicitudId) {
            Optional<SolicitudAmistad> solicitudOpt = solicitudAmistadRepository.findById(solicitudId);
            if (solicitudOpt.isPresent()) {
                SolicitudAmistad solicitud = solicitudOpt.get();
                String destinatarioEmail = authentication.getName();
                if (!solicitud.getDestinatario().getEmail().equals(destinatarioEmail)) {
                    throw new RuntimeException("Solicitud no pertenece al usuario destinatario.");
                }

                solicitud.setEstado(SolicitudAmistad.EstadoSolicitud.ACEPTADA);
                solicitudAmistadRepository.save(solicitud);

                Usuario remitente = solicitud.getRemitente();
                Usuario destinatario = solicitud.getDestinatario();
                remitente.getAmigos().add(destinatario);
                destinatario.getAmigos().add(remitente);

                usuarioRepository.save(remitente);
                usuarioRepository.save(destinatario);
            }

            return "redirect:/amigos";
        }

        @PostMapping("/rechazarSolicitud")
        @ResponseBody
        public ResponseEntity<String> rechazarSolicitud(@RequestParam Long solicitudId, Principal principal) {
            usuarioService.rechazarSolicitud(principal.getName(), solicitudId);
            return ResponseEntity.ok("Solicitud rechazada");
        }

        @PostMapping("/eliminarAmigo/{amigoId}")
        @ResponseBody
        public ResponseEntity<String> eliminarAmigo(@PathVariable Long amigoId) {
            try {
                usuarioService.eliminarAmigo(amigoId);
                return ResponseEntity.ok("Amigo eliminado correctamente");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar amigo: " + e.getMessage());
            }
        }
    }