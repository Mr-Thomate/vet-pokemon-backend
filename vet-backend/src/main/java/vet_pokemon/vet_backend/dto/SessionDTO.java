package vet_pokemon.vet_backend.dto;

import lombok.Data;

@Data
public class SessionDTO {
    private String token;
    private Integer idUsuario;
    private String nombre;
    private String rol;
}