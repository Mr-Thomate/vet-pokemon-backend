package vet_pokemon.vet_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class VentaDTO {


    private Integer id;
    private LocalDateTime fecha;
    private Integer total;
    
    private String nombreUsuario;
    private String nombreEstado;
    private String nombreMetodoPago;
    private String nombreMetodoEnvio;
    private List<DetalleVentaDTO> detalles;
}