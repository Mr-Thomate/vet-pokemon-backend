package vet_pokemon.vet_backend.DTO;
import lombok.Data;

@Data 
public class MetodoPagoDTO {
    private Integer id_MetodoPago;
    private String descripcion;
    private String nombremetodopago;
    private Integer Monto_total;
}