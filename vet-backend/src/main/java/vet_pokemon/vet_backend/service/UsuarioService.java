import vet_pokemon.vet_backend.service;
import vet_pokemon.vet_backend.config.WebClientConfig;
import vet_pokemon.vet_backend.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WebClientConfig webClientConfig;

    private UsuarioDTO convertirADTO(Usuario user){
        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(user.getId());
        String nombreCompleto = "";
        try {
            nombreCompleto = nombreCompleto + user.getPnombre();
            if (user.getSnombre() != ""){
                
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        dto.setNombre(user.getPnombre()+user.getSnombre()+user.getPapellido()+user.getSapellido());
        
    }

}