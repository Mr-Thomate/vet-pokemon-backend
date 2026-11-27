package vet_pokemon.vet_backend.DTO;

import lombok.Data;

@Data
public class LoginDTO {
    private String correo;
    private String contraseña;
}