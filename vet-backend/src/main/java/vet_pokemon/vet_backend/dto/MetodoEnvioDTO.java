import java.time.LocalDate;

import lombok.Data;

@Data
public class MetodoEnvioDTO {

    private Integer id_Envio;
    private LocalDate fechaSalida;
    private LocalDate fechaEntrega;
    private String tipoEnvio;
    private String sucursal;


}