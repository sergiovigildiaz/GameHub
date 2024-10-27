package com.example.demo.service;

import com.example.demo.model.Mensaje;
import com.example.demo.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    public Mensaje enviarMensaje(Mensaje mensaje) {
        mensaje.setFechaEnvio(LocalDateTime.now());
        return mensajeRepository.save(mensaje);
    }

    public List<Mensaje> obtenerMensajes(String remitenteId, String destinatarioId) {
        return mensajeRepository.findByRemitenteIdAndDestinatarioId(remitenteId, destinatarioId);
    }

    public List<Mensaje> obtenerMensajesRecibidos(String destinatarioId) {
        return mensajeRepository.findByDestinatarioId(destinatarioId);
    }
}