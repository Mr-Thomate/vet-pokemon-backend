package vet_pokemon.vet_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.repository.UsuarioRepository;
import vet_pokemon.vet_backend.model.Usuario;
import vet_pokemon.vet_backend.dto.UsuarioDTO;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Get all
    public List<UsuarioDTO> obtenerTodos(){
        return usuarioRepository.findAll().stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
    }

    // Buscar por ID
    public UsuarioDTO buscarPorId(Integer id){
        return usuarioRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Error: No se encontro el usuario con ID: " + id));
    }

    // Guardar
    public Usuario guardar(Usuario user){
        return usuarioRepository.save(user);
    }

    // Eliminar no se usará.

    // Actualizar
    public Usuario actualizar(Integer id, Usuario usuario){
        Usuario user = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Error: El usuario con ID: " + id + " no existe!"));
        if(usuario.getPnombre() != null){
            user.setPnombre(usuario.getPnombre());
        }
        if(usuario.getSnombre() != null){
            user.setSnombre(usuario.getSnombre());
        }
        if(usuario.getPapellido() != null){
            user.setPapellido(usuario.getPapellido());
        }
        if(usuario.getSapellido() != null){
            user.setSapellido(usuario.getSapellido());
        }
        if(usuario.getFechaNacimiento() != null){
            user.setFechaNacimiento(usuario.getFechaNacimiento());
        }
        if(usuario.getCorreo() != null){
            user.setCorreo(usuario.getCorreo());
        }
        if(usuario.getRol() != null){
            user.setRol(usuario.getRol());
        }
        if(usuario.getComuna() != null){
            user.setComuna(usuario.getComuna());
        }

        return usuarioRepository.save(user);
    }

    // Conversor de DTO
    private UsuarioDTO convertirADTO(Usuario user){
        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(user.getId());

        // Manejo de nombre: primer nombre + (segundo nombre) + primer apellido + (segundo apellido)
        // Los campos opcionales (snombre y sapellido) pueden venir null, por eso se revisan antes de usarlos.
        String nombreCompleto = user.getPnombre();
        if (user.getSnombre() != null && !user.getSnombre().isBlank()){
            nombreCompleto += " " + user.getSnombre();
        }
        nombreCompleto += " " + user.getPapellido();
        if (user.getSapellido() != null && !user.getSapellido().isBlank()){
            nombreCompleto += " " + user.getSapellido();
        }
        dto.setNombre(nombreCompleto);

        // Manejo de fecha nacimiento
        dto.setFechaNacimiento(user.getFechaNacimiento().toString());

        // Manejo del nombre del rol (el rol puede ser null)
        if (user.getRol() != null){
            dto.setNombreRol(user.getRol().getNombreRol());
        }

        dto.setCorreo(user.getCorreo());

        // Manejo del nombre de la comuna (la comuna puede ser null)
        if (user.getComuna() != null){
            dto.setNombrecomuna(user.getComuna().getNombreComuna());
        }

        return dto;
    }

}
