package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/noticias")
    public String getNoticias(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> noticias = newsService.getVideoGameNews();
        model.addAttribute("noticias", noticias.get("articles"));

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        model.addAttribute("urlFotoPerfil", usuario.getFotoPerfil());
        return "noticias";
    }


}