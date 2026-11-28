package vet_pokemon.vet_backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class BlogDTO {
    private Integer id;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaPublicacion;
    private List<String> imagenes;
}