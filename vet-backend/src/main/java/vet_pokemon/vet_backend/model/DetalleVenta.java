package vet_pokemon.vet_backend.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "detalles_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_DetalleVenta;

    @NotBlank(message = "La descripción del detalle es obligatoria")
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "La fecha de venta es obligatoria")
    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta;

    @NotNull(message = "El total de la venta es obligatorio")
    @Column(name = "total_venta", nullable = false)
    private Integer totalVenta;

    @NotBlank(message = "La sucursal es obligatoria")
    @Column(nullable = false, length = 100)
    private String sucursal;

    // A qué venta pertenece este detalle (columna venta_id)
    @ManyToOne
    @JoinColumn(name = "venta_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Venta venta;
}
