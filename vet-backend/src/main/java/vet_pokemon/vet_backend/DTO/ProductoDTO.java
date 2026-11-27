package vet_pokemon.vet_backend.DTO;

import lombok.Data;
import java.util.List;

@Data 
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Integer stock;
    private String nombreCategoria;
    private String nombreTipo;
    private List<String> imagenes;
    
}
