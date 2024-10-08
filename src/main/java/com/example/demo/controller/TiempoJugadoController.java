package com.example.demo.controller;

import com.example.demo.model.BibliotecaJuego;
import com.example.demo.model.TiempoJugado;
import com.example.demo.model.Usuario;
import com.example.demo.repository.BibliotecaJuegoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.BibliotecaJuegoService;
import com.example.demo.service.TiempoJugadoService;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tiempojugado")
public class TiempoJugadoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BibliotecaJuegoRepository bibliotecaJuegoRepository;

    @Autowired
    private TiempoJugadoService tiempoJugadoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BibliotecaJuegoService bibliotecaJuegoService;

    @PostMapping("/iniciar")
    public TiempoJugado iniciarTiempoJugado(@RequestParam Long usuarioId, @RequestParam Long juegoId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        BibliotecaJuego juego = bibliotecaJuegoService.obtenerJuegoPorId(juegoId);
        return tiempoJugadoService.iniciarJuego(usuario, juego);
    }

    @PostMapping("/terminar/{tiempoJugadoId}")
    public ResponseEntity<TiempoJugado> terminarJuego(@PathVariable Long tiempoJugadoId) {
        TiempoJugado tiempoJugado = tiempoJugadoService.terminarJuego(tiempoJugadoId);
        return ResponseEntity.ok(tiempoJugado);
    }

}