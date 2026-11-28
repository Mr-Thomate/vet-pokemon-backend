package vet_pokemon.vet_backend.model;

import java.time.LocalDate;

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
@Table(name = "metodos_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_Envio;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @NotNull(message = "La fecha de entrega es obligatoria")
    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    @NotBlank(message = "El tipo de envío es obligatorio")
    @Column(name = "tipo_envio", nullable = false, length = 100)
    private String tipoEnvio;

    @NotBlank(message = "La sucursal es obligatoria")
    @Column(nullable = false, length = 100)
    private String sucursal;
}