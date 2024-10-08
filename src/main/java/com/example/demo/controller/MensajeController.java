package com.example.demo.controller;

import com.example.demo.dto.MensajeDTO;
import com.example.demo.model.Mensaje;
import com.example.demo.model.Usuario;
import com.example.demo.repository.MensajeRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.MensajeService;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/mensajes")
public class MensajeController {

    @Autowired
    private UsuarioService usuarioService;



}