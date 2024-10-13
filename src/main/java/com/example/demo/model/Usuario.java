package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "pais")
    private String pais;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "contrasenia")
    private String contrasenia;

    @Column(name = "fotoPerfil")
    private String fotoPerfil;

    @ManyToMany
    @JoinTable(
            name = "amigos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    @ToString.Exclude
    private Set<Usuario> amigos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "solicitudes_amistad",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    @ToString.Exclude
    private Set<Usuario> solicitudesAmistadEnviadas = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "solicitudes_amistad_recibidas",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    @ToString.Exclude
    private Set<Usuario> solicitudesAmistadRecibidas = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BibliotecaJuego> biblioteca = new ArrayList<>();

    public boolean esAmigo(Usuario otroUsuario) {
        return this.amigos.contains(otroUsuario);
    }
}
