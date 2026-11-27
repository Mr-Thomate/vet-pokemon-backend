package vet_pokemon.vet_backend.DTO;

import lombok.Data;

@Data 
public class UsuarioDTO {

    private Integer id;
    private String nombre;
    private String fechaNacimiento;
    private String nombreRol;
    private String correo;
    private String nombrecomuna;
}
