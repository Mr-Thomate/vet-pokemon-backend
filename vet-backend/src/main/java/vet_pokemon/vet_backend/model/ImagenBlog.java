package vet_pokemon.vet_backend.model;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagenes_blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Column(nullable = false, length = 500)
    private String url;

    @NotNull(message = "El blog es obligatorio")
    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;
}