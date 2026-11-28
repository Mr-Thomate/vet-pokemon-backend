package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.TipoDTO;
import vet_pokemon.vet_backend.model.Tipo;
import vet_pokemon.vet_backend.repository.TipoRepository;

@Service
@Transactional
public class TipoService {

    @Autowired
    private TipoRepository tipoRepository;

    private TipoDTO convertirADTO(Tipo tipo) {
        TipoDTO dto = new TipoDTO();

        dto.setId(tipo.getId());
        dto.setNombre(tipo.getNombre());

        return dto;
    }

    public List<TipoDTO> listarTipos() {
        List<Tipo> tipos = tipoRepository.findAll();
        List<TipoDTO> listaDTO = new ArrayList<>();

        for (Tipo tipo : tipos) {
            listaDTO.add(convertirADTO(tipo));
        }
        return listaDTO;
    }

    public TipoDTO obtenerTipoPorId(Integer id) {
        Tipo tipo = tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado con ID: " + id));
        return convertirADTO(tipo);
    }

    public TipoDTO guardarTipo(Tipo tipo) {
        if (tipoRepository.existsByNombreIgnoreCase(tipo.getNombre())) {
            throw new RuntimeException("El tipo ya se encuentra registrado");
        }
        Tipo tipoGuardado = tipoRepository.save(tipo);
        return convertirADTO(tipoGuardado);
    }

    public TipoDTO actualizarTipo(Integer id, Tipo datosActualizados) {
        Tipo tipoExistente = tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrado con ID: " + id));

        tipoExistente.setNombre(datosActualizados.getNombre());

        Tipo tipoActualizado = tipoRepository.save(tipoExistente);
        return convertirADTO(tipoActualizado);
    }

    public void eliminarTipo(Integer id) {
        if (!tipoRepository.existsById(id)) {
            throw new RuntimeException("Tipo no encontrado con ID: " + id);
        }
        tipoRepository.deleteById(id);
    }
}