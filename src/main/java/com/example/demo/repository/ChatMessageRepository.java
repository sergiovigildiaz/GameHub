package com.example.demo.repository;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Mensaje;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Cambia el nombre del método
    List<ChatMessage> findByRemitenteAndDestinatario(Usuario remitente, Usuario destinatario);

    // Nuevo método para la consulta inversa
    List<ChatMessage> findByDestinatarioAndRemitente(Usuario destinatario, Usuario remitente);
}
