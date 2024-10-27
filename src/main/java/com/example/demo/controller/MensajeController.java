package com.example.demo.controller;

import com.example.demo.model.Mensaje;
import com.example.demo.repository.MensajeRepository;
import com.example.demo.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @MessageMapping("/envio") // Ruta que se usará en el WebSocket
    @SendTo("/tema/mensajes") // Dónde se enviarán los mensajes
    public Mensaje enviarMensaje(Mensaje mensaje) {
        // Procesar y devolver el mensaje
        return mensaje;
    }

    // Si necesitas una ruta REST para obtener mensajes
    @PostMapping("/obtenerMensajes")
    public List<Mensaje> obtenerMensajes(@RequestBody String remitenteId, String destinatarioId) {
        return mensajeService.obtenerMensajes(remitenteId, destinatarioId);
    }
}