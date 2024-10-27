package com.example.demo.repository;

import com.example.demo.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByRemitenteIdAndDestinatarioId(String remitenteId, String destinatarioId);
    List<Mensaje> findByDestinatarioId(String destinatarioId); // Para obtener mensajes a un destinatario
}