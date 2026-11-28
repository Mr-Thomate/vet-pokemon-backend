package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.RolDTO;
import vet_pokemon.vet_backend.model.Rol;
import vet_pokemon.vet_backend.repository.RolRepository;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    private RolDTO convertirADTO(Rol rol) {
        RolDTO dto = new RolDTO();

        dto.setId(rol.getId());
        dto.setNombrerol(rol.getNombreRol());

        return dto;
    }

    public List<RolDTO> listarRoles() {
        List<Rol> roles = rolRepository.findAll();
        List<RolDTO> listaDTO = new ArrayList<>();

        for (Rol rol : roles) {
            listaDTO.add(convertirADTO(rol));
        }
        return listaDTO;
    }

    public RolDTO obtenerRolPorId(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        return convertirADTO(rol);
    }

    public RolDTO guardarRol(Rol rol) {
        if (rolRepository.existsByNombreRolIgnoreCase(rol.getNombreRol())) {
            throw new RuntimeException("El rol ya se encuentra registrado");
        }
        Rol rolGuardado = rolRepository.save(rol);
        return convertirADTO(rolGuardado);
    }

    public RolDTO actualizarRol(Integer id, Rol datosActualizados) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        rolExistente.setNombreRol(datosActualizados.getNombreRol());

        Rol rolActualizado = rolRepository.save(rolExistente);
        return convertirADTO(rolActualizado);
    }

    public void eliminarRol(Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        rolRepository.deleteById(id);
    }
}