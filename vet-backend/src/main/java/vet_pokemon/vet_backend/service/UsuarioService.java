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
        if(usuario.getRol() != null){
            user.setRol(usuario.getRol());
        }

        return usuarioRepository.save(user);
    }

    // Conversor de DTO
    private UsuarioDTO convertirADTO(Usuario user){
        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(user.getId());

        // Manejo de nombre
        String nombreCompleto = "";
        try {
            nombreCompleto = nombreCompleto + user.getPnombre();
            if (user.getSnombre() != ""){
                nombreCompleto += " " + user.getSnombre();
            }
            nombreCompleto += " " + user.getPapellido();
            if (user.getSapellido() != ""){
                nombreCompleto += " " + user.getSapellido();
            }

        } catch (Exception e) {
            nombreCompleto = "Invalido";
        }

        // Manejo de fecha nacimiento
        String strFecNac = user.getFechaNacimiento().toString();
        dto.setFechaNacimiento(strFecNac);

        // Manejo del nombre del rol
        String nombreRol = user.getRol().getNombreRol();
        dto.setNombreRol(nombreRol);

        return dto;
    }

}