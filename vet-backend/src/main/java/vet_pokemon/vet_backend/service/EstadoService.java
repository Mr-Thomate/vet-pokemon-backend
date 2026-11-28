package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.EstadoDTO;
import vet_pokemon.vet_backend.model.Estado;
import vet_pokemon.vet_backend.repository.EstadoRepository;

@Service
@Transactional
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    private EstadoDTO convertirADTO(Estado estado) {
        EstadoDTO dto = new EstadoDTO();

        dto.setId_estado(estado.getId_estado());
        dto.setDescripcion(estado.getDescripcion());

        return dto;
    }

    public List<EstadoDTO> listarEstados() {
        List<Estado> estados = estadoRepository.findAll();
        List<EstadoDTO> listaDTO = new ArrayList<>();

        for (Estado estado : estados) {
            listaDTO.add(convertirADTO(estado));
        }
        return listaDTO;
    }

    public EstadoDTO obtenerEstadoPorId(Integer id) {
        Estado estado = estadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + id));
        return convertirADTO(estado);
    }

    public List<EstadoDTO> buscarPorDescripcion(String descripcion) {
        List<Estado> estados = estadoRepository.findByDescripcion(descripcion);
        List<EstadoDTO> listaDTO = new ArrayList<>();

        for (Estado estado : estados) {
            listaDTO.add(convertirADTO(estado));
        }
        return listaDTO;
    }

    public EstadoDTO guardarEstado(Estado estado) {
        if (!estadoRepository.findByDescripcion(estado.getDescripcion()).isEmpty()) {
            throw new RuntimeException("El estado ya se encuentra registrado");
        }
        Estado estadoGuardado = estadoRepository.save(estado);
        return convertirADTO(estadoGuardado);
    }

    public EstadoDTO actualizarEstado(Integer id, Estado datosActualizados) {
        Estado estadoExistente = estadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + id));

        estadoExistente.setDescripcion(datosActualizados.getDescripcion());

        Estado estadoActualizado = estadoRepository.save(estadoExistente);
        return convertirADTO(estadoActualizado);
    }

    public void eliminarEstado(Integer id) {
        if (!estadoRepository.existsById(id)) {
            throw new RuntimeException("Estado no encontrado con ID: " + id);
        }
        estadoRepository.deleteById(id);
    }
}