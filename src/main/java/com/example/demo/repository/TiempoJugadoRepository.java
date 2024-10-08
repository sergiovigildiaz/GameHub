package com.example.demo.repository;

import com.example.demo.model.Mensaje;
import com.example.demo.model.TiempoJugado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiempoJugadoRepository extends JpaRepository<TiempoJugado, Long> {
}
