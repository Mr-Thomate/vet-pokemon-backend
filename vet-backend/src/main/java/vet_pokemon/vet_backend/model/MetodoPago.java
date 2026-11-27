package vet_pokemon.vet_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metodos_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_MetodoPago;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotBlank(message = "El nombre del método de pago es obligatorio")
    @Column(name = "nombre_metodo_pago", nullable = false, length = 100)
    private String nombremetodopago;

    @NotNull(message = "El monto total es obligatorio")
    @Column(name = "monto_total", nullable = false)
    private Integer Monto_total;
}