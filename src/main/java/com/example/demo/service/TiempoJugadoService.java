package com.example.demo.service;

import com.example.demo.model.BibliotecaJuego;
import com.example.demo.model.TiempoJugado;
import com.example.demo.model.Usuario;
import com.example.demo.repository.TiempoJugadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TiempoJugadoService {

    @Autowired
    private TiempoJugadoRepository tiempoJugadoRepository;

    public TiempoJugado iniciarJuego(Usuario usuario, BibliotecaJuego juego) {
        TiempoJugado tiempoJugado = new TiempoJugado();
        tiempoJugado.setUsuario(usuario);
        tiempoJugado.setJuego(juego);
        tiempoJugado.setInicio(LocalDateTime.now());
        return tiempoJugadoRepository.save(tiempoJugado);
    }

    public TiempoJugado terminarJuego(Long tiempoJugadoId) {
        Optional<TiempoJugado> optionalTiempoJugado = tiempoJugadoRepository.findById(tiempoJugadoId);
        if (optionalTiempoJugado.isPresent()) {
            TiempoJugado tiempoJugado = optionalTiempoJugado.get();
            tiempoJugado.setFin(LocalDateTime.now());
            return tiempoJugadoRepository.save(tiempoJugado);
        }
        throw new IllegalArgumentException("Tiempo jugado no encontrado con ID: " + tiempoJugadoId);
    }
}