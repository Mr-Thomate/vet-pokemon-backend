package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.ComunaDTO;
import vet_pokemon.vet_backend.model.Comuna;
import vet_pokemon.vet_backend.repository.ComunaRepository;

@Service
@Transactional
public class ComunaService {

    @Autowired
    private ComunaRepository comunaRepository;

    private ComunaDTO convertirADTO(Comuna comuna) {
        ComunaDTO dto = new ComunaDTO();

        dto.setId(comuna.getId());
        dto.setNombrecomuna(comuna.getNombreComuna());

        if (comuna.getRegion() != null) {
            dto.setNombreRegion(comuna.getRegion().getNombreRegion());
        }

        return dto;
    }

    public List<ComunaDTO> listarComunas() {
        List<Comuna> comunas = comunaRepository.findAll();
        List<ComunaDTO> listaDTO = new ArrayList<>();

        for (Comuna comuna : comunas) {
            listaDTO.add(convertirADTO(comuna));
        }
        return listaDTO;
    }

    public ComunaDTO obtenerComunaPorId(Integer id) {
        Comuna comuna = comunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comuna no encontrada con ID: " + id));
        return convertirADTO(comuna);
    }

    public List<ComunaDTO> listarPorRegion(Integer idRegion) {
        List<Comuna> comunas = comunaRepository.findByIdRegion(idRegion);
        List<ComunaDTO> listaDTO = new ArrayList<>();

        for (Comuna comuna : comunas) {
            listaDTO.add(convertirADTO(comuna));
        }
        return listaDTO;
    }

    public ComunaDTO guardarComuna(Comuna comuna) {
        Comuna comunaGuardada = comunaRepository.save(comuna);
        return convertirADTO(comunaGuardada);
    }

    public ComunaDTO actualizarComuna(Integer id, Comuna datosActualizados) {
        Comuna comunaExistente = comunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comuna no encontrada con ID: " + id));

        comunaExistente.setNombreComuna(datosActualizados.getNombreComuna());

        if (datosActualizados.getRegion() != null) {
            comunaExistente.setRegion(datosActualizados.getRegion());
        }

        Comuna comunaActualizada = comunaRepository.save(comunaExistente);
        return convertirADTO(comunaActualizada);
    }

    public void eliminarComuna(Integer id) {
        if (!comunaRepository.existsById(id)) {
            throw new RuntimeException("Comuna no encontrada con ID: " + id);
        }
        comunaRepository.deleteById(id);
    }
}
