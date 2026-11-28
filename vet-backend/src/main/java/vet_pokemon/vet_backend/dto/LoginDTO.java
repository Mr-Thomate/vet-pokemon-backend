package vet_pokemon.vet_backend.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String correo;
    private String contraseña;
}