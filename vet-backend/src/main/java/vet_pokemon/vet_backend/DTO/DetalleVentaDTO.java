package vet_pokemon.vet_backend.DTO;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DetalleVentaDTO {

    private Integer id_DetalleVenta;
    private String descripcion;
    private LocalDate fechaVenta;
    private Integer TotalVenta;
    private String sucursal;


}