package com.example.demo.controller;

import com.example.demo.dto.UsuarioLoginDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.BibliotecaJuegoService;
import com.example.demo.service.GameService;
import com.example.demo.service.NewsService;
import com.example.demo.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private GameService gameService;
    @Autowired
    private BibliotecaJuegoService bibliotecaJuegoService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByEmail(email);
            String nombreUsuario = usuario.getNombre();
            String fotoPerfil = usuario.getFotoPerfil();

            if (fotoPerfil == null || fotoPerfil.isEmpty()) {
                fotoPerfil = "https://via.placeholder.com/150";
            }

            model.addAttribute("nombreUsuario", nombreUsuario);
            model.addAttribute("urlFotoPerfil", fotoPerfil);
            model.addAttribute("usuarioId", usuario.getId());

            // Amigos
            // Obtener la lista de amigos del usuario
            Set<Usuario> amigos = usuario.getAmigos();

            // Seleccionar hasta tres amigos de manera aleatoria
            List<Usuario> amigosAleatorios = new ArrayList<>();
            if (amigos.size() > 0) {
                Random random = new Random();
                amigosAleatorios = amigos.stream()
                        .skip(random.nextInt(amigos.size()))
                        .limit(3)
                        .collect(Collectors.toList());
            }
            model.addAttribute("amigos", amigosAleatorios);

            try {
                Map<String, Object> noticias = newsService.getVideoGameNews();
                List<Map<String, Object>> articles = (List<Map<String, Object>>) noticias.get("articles");
                List<Map<String, Object>> ultimasNoticias = articles.subList(0, Math.min(articles.size(), 3));
                model.addAttribute("ultimasNoticias", ultimasNoticias);
            } catch (Exception e) {
                System.out.println("Error al obtener noticias: " + e.getMessage());
                model.addAttribute("ultimasNoticias", Collections.emptyList());
                model.addAttribute("errorNoticias", "No se pudieron cargar las noticias en este momento. Por favor, intenta más tarde.");
            }

            try {
                List<Map<String, Object>> juegos = gameService.getThreeRandomGames();
                model.addAttribute("juegos", juegos);
            } catch (Exception e) {
                System.out.println("Error al obtener juegos aleatorios: " + e.getMessage());
                model.addAttribute("juegos", Collections.emptyList());
                model.addAttribute("errorJuegos", "No se pudieron cargar los juegos en este momento. Por favor, intenta más tarde.");
            }

            return "home";
        } else {
            return "redirect:/login"; // Asegúrate de redirigir a login si no está autenticado
        }
    }

    @PostMapping("/")
    public String loginPost(HttpServletRequest request, @ModelAttribute UsuarioLoginDTO usuarioLoginDTO, Model model) {
        try {
            Usuario usuario = usuarioService.login(usuarioLoginDTO); // Verificar las credenciales del usuario
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true); // Establecer la autenticación en la sesión
            session.setAttribute("nombreUsuario", usuario.getNombre()); // Guardar el nombre del usuario en la sesión
            session.setAttribute("usuarioId", (Integer) usuario.getId()); // Guardar el ID del usuario en la sesión
            session.setAttribute("urlFotoPerfil", usuario.getFotoPerfil());
            return "redirect:/"; // Redirigir a la página de inicio después del inicio de sesión exitoso
        } catch (RuntimeException e) {
            model.addAttribute("error", "Credenciales inválidas"); // Agregar un mensaje de error al modelo
            return "login"; // Volver a cargar la página de inicio de sesión con un mensaje de error
        }
    }

}