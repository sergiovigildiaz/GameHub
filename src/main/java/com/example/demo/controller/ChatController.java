package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Mensaje;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.MensajeRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // Establecer la fecha y hora actual
        message.setTimestamp(LocalDateTime.now());

        // Obtener el ID del usuario desde el contexto de seguridad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Asumiendo que el nombre del usuario es su ID. Si no es así, obtén el ID adecuado.

        // Buscando el usuario en la base de datos
        Usuario remitente = usuarioRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));

        // Comprobar si el destinatario está presente
        if (message.getDestinatario() == null) {
            throw new RuntimeException("Destinatario no encontrado");
        }

        // Asignar el remitente al mensaje
        message.setRemitente(remitente);

        // Guardar el mensaje en la base de datos
        chatMessageRepository.save(message);
        return message;
    }

    @GetMapping("/mensajes/{id1}/{id2}")
    @ResponseBody
    public List<ChatMessage> obtenerMensajes(@PathVariable Long id1, @PathVariable Long id2) {
        Usuario remitente = usuarioRepository.findById(id1)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario destinatario = usuarioRepository.findById(id2)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        // Realiza ambas consultas y combina los resultados
        List<ChatMessage> mensajes = chatMessageRepository.findByRemitenteAndDestinatario(remitente, destinatario);
        mensajes.addAll(chatMessageRepository.findByRemitenteAndDestinatario(destinatario, remitente));
        return mensajes;
    }
}