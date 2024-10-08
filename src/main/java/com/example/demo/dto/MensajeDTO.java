package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensajeDTO {
    private Long id;
    private Long usuarioId;
    private String contenido;
    private LocalDateTime fechaCreacion;
}