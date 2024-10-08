package com.example.demo.controller;

import com.example.demo.dto.BibliotecaJuegoDTO;
import com.example.demo.model.BibliotecaJuego;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.BibliotecaJuegoService;
import com.example.demo.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class GameController {

    @Value("${rawg.apiKey}")
    private String apiKey;

    @Autowired
    private GameService gameService;

    @Autowired
    private BibliotecaJuegoService bibliotecaJuegoService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/juegos")
    public String mostrarJuegos(
            @RequestParam(value = "plataforma", required = false) String plataforma,
            @RequestParam(value = "genero", required = false) String genero,
            @PageableDefault(size = 20) Pageable pageable,
            Model model, Authentication authentication) {
        try {
            // Obtener todos los juegos desde el servicio con paginación y filtros
            Page<Map<String, Object>> paginaJuegos = gameService.getAllGamesFiltered(pageable, genero, plataforma);
            List<Map<String, Object>> juegos = paginaJuegos.getContent();
            // Filtrar por plataforma si se proporciona el parámetro
            if (plataforma != null && !plataforma.isEmpty()) {
                juegos = juegos.stream()
                        .filter(juego -> juego.get("platforms").toString().contains(plataforma))
                        .collect(Collectors.toList());
            }

            // Filtrar por género si se proporciona el parámetro
            if (genero != null && !genero.isEmpty()) {
                juegos = juegos.stream()
                        .filter(juego -> juego.get("genres").toString().contains(genero))
                        .collect(Collectors.toList());
            }

            // Obtener el usuario autenticado
            String correoElectronico;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                correoElectronico = userDetails.getUsername();
            } else {
                throw new RuntimeException("Usuario no autenticado");
            }

            Usuario usuario = usuarioRepository.findByEmail(correoElectronico);
            List<BibliotecaJuego> biblioteca = bibliotecaJuegoService.getBibliotecaByEmail(usuario.getEmail());

            // Marcar juegos que están en la biblioteca del usuario
            juegos.forEach(juego -> {
                boolean enBiblioteca = biblioteca.stream().anyMatch(b -> b.getNombreJuego().equals(juego.get("name")));
                juego.put("enBiblioteca", enBiblioteca);
            });

            model.addAttribute("urlFotoPerfil", usuario.getFotoPerfil());
            model.addAttribute("juegos", juegos);
            model.addAttribute("pagina", paginaJuegos);
            model.addAttribute("usuario", usuario);
        } catch (Exception e) {
            // Manejo de errores
            System.out.println("Error al obtener juegos: " + e.getMessage());
            model.addAttribute("juegos", Collections.emptyList());
            model.addAttribute("error", "No se pudieron cargar los juegos en este momento. Por favor, intenta más tarde.");
        }
        return "juegos";
    }

    @GetMapping("/juegos-aleatorios")
    public String mostrarJuegosAleatorios(Model model) {
        // Obtener tres juegos aleatorios
        List<Map<String, Object>> juegosAleatorios = gameService.getThreeRandomGames();
        model.addAttribute("juegosAleatorios", juegosAleatorios);
        return "juegos-aleatorios"; // Nombre de la vista que mostrará los juegos aleatorios
    }

    @PostMapping("/addJuego")
    public String addJuego(HttpSession session, @ModelAttribute BibliotecaJuegoDTO bibliotecaJuegoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String correoElectronico = userDetails.getUsername();

        Usuario usuario = usuarioRepository.findByEmail(correoElectronico);

        bibliotecaJuegoService.addJuegoToBiblioteca((long) usuario.getId(), bibliotecaJuegoDTO);

        return "redirect:/juegos";
    }

    @PostMapping("/eliminarJuego")
    public String eliminarJuego(@RequestParam("juegoId") Long juegoId) {
        bibliotecaJuegoService.eliminarJuego(juegoId);
        return "redirect:/juegos";
    }

    @GetMapping("/juego/{id}")
    @ResponseBody
    public Map<String, Object> obtenerJuegoPorId(@PathVariable("id") int id) {
        String gameUrl = "https://api.rawg.io/api/games/" + id + "?key=" + apiKey;
        String screenshotsUrl = "https://api.rawg.io/api/games/" + id + "/screenshots?key=" + apiKey;
        String storesUrl = "https://api.rawg.io/api/games/" + id + "/stores?key=" + apiKey;
        String trailersUrl = "https://api.rawg.io/api/games/" + id + "/movies?key=" + apiKey;

        Map<String, Object> gameDetails = restTemplate.getForObject(gameUrl, Map.class);
        Map<String, Object> screenshots = restTemplate.getForObject(screenshotsUrl, Map.class);
        Map<String, Object> stores = restTemplate.getForObject(storesUrl, Map.class);
        Map<String, Object> trailers = restTemplate.getForObject(trailersUrl, Map.class);

        gameDetails.put("screenshots", screenshots.get("results"));
        gameDetails.put("stores", stores.get("results"));
        gameDetails.put("trailers", trailers.get("results"));

        return gameDetails;
    }

    @GetMapping("/buscarJuegos")
    public String buscarJuegos(@RequestParam("q") String query, Model model, Authentication authentication) {
        String url = "https://api.rawg.io/api/games?key=" + apiKey + "&search=" + query;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> juegos = (List<Map<String, Object>>) response.get("results");

            // Obtener el usuario autenticado
            String correoElectronico;
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                correoElectronico = userDetails.getUsername();
            } else {
                throw new RuntimeException("Usuario no autenticado");
            }

            Usuario usuario = usuarioRepository.findByEmail(correoElectronico);
            List<BibliotecaJuego> biblioteca = bibliotecaJuegoService.getBibliotecaByEmail(usuario.getEmail());

            juegos.forEach(juego -> {
                boolean enBiblioteca = biblioteca.stream().anyMatch(b -> b.getNombreJuego().equals(juego.get("name")));
                juego.put("enBiblioteca", enBiblioteca);
            });

            model.addAttribute("urlFotoPerfil", usuario.getFotoPerfil());
            model.addAttribute("juegos", juegos);
            model.addAttribute("usuario", usuario);
        } catch (Exception e) {
            System.out.println("Error al buscar juegos: " + e.getMessage());
            model.addAttribute("juegos", Collections.emptyList());
            model.addAttribute("error", "No se pudieron cargar los juegos en este momento. Por favor, intenta más tarde.");
        }

        return "juegos";
    }
}