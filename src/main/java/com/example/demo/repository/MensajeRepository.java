package com.example.demo.repository;

import com.example.demo.model.Mensaje;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByRemitenteIdAndDestinatarioIdOrRemitenteIdAndDestinatarioId(
            String remitenteId1,
            String destinatarioId1,
            String remitenteId2,
            String destinatarioId2
    );
}