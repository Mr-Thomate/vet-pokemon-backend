import vet_pokemon.vet_backend.model;

@Data
@Entity
@Table(name="rol")
@AllArgsConstructor
@NoArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El rol debe tener un nombre")
    @Size(max=20, message= "El nombre del rol no debe superar los 20 caracteres.")
    @Column(nullable=false, lenght=20)
    private String nombreRol;

}