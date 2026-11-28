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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Usuario")
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El usuario debe tener primer nombre")
    @Size(min = 3, max = 15, message = "El primer nombre debe tener entre 3 y 15 caracteres.")
    @Column(nullable = false, length = 15)
    private String pnombre;

    @Column(nullable = true, length = 15)
    private String snombre;

    @NotBlank(message = "El usuario debe tener primer apellido")
    @Size(min = 3, max = 15, message = "El primer apellido debe tener entre 3 y 15 caracteres.")
    @Column(nullable = false, length = 15)
    private String papellido;

    @Column(nullable = true, length = 15)
    private String sapellido;

    @NotNull(message = "El usuario debe registrar su fecha de nacimiento")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Lo usan UsuarioRepository (existsByCorreoIgnoreCase) y UsuarioDTO
    @NotBlank(message = "El usuario debe tener un correo")
    @Email(message = "El correo no tiene un formato válido")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    // Lo usa UsuarioDTO (nombrecomuna)
    @ManyToOne
    @JoinColumn(name = "comuna_id")
    private Comuna comuna;

}
