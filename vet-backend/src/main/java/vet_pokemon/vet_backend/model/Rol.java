package vet_pokemon.vet_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Entity 
@Table (name="rol")
@AllArgsConstructor 
@NoArgsConstructor 
public class Rol {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank (message = "El rol debe tener un nombre")
    @Size (max=20, message= "El nombre del rol no debe superar los 20 caracteres.")
    @Column (nullable=false, length = 20)
    private String nombreRol;

}