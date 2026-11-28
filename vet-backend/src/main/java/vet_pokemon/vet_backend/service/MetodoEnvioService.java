package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.MetodoEnvioDTO;
import vet_pokemon.vet_backend.model.MetodoEnvio;
import vet_pokemon.vet_backend.repository.MetodoEnvioRepository;

@Service
@Transactional
public class MetodoEnvioService {

    @Autowired
    private MetodoEnvioRepository metodoEnvioRepository;

    private MetodoEnvioDTO convertirADTO(MetodoEnvio metodoEnvio) {
        MetodoEnvioDTO dto = new MetodoEnvioDTO();

        dto.setId_Envio(metodoEnvio.getId_Envio());
        dto.setFechaSalida(metodoEnvio.getFechaSalida());
        dto.setFechaEntrega(metodoEnvio.getFechaEntrega());
        dto.setTipoEnvio(metodoEnvio.getTipoEnvio());
        dto.setSucursal(metodoEnvio.getSucursal());

        return dto;
    }

    public List<MetodoEnvioDTO> listarMetodosEnvio() {
        List<MetodoEnvio> metodos = metodoEnvioRepository.findAll();
        List<MetodoEnvioDTO> listaDTO = new ArrayList<>();

        for (MetodoEnvio metodo : metodos) {
            listaDTO.add(convertirADTO(metodo));
        }
        return listaDTO;
    }

    public MetodoEnvioDTO obtenerMetodoEnvioPorId(Integer id) {
        MetodoEnvio metodoEnvio = metodoEnvioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metodo de envio no encontrado con ID: " + id));
        return convertirADTO(metodoEnvio);
    }

    public List<MetodoEnvioDTO> buscarPorTipoEnvio(String tipoEnvio) {
        List<MetodoEnvio> metodos = metodoEnvioRepository.findByTipoEnvio(tipoEnvio);
        List<MetodoEnvioDTO> listaDTO = new ArrayList<>();

        for (MetodoEnvio metodo : metodos) {
            listaDTO.add(convertirADTO(metodo));
        }
        return listaDTO;
    }

    public MetodoEnvioDTO guardarMetodoEnvio(MetodoEnvio metodoEnvio) {
        if (!metodoEnvioRepository.findByTipoEnvio(metodoEnvio.getTipoEnvio()).isEmpty()) {
            throw new RuntimeException("El metodo de envio ya se encuentra registrado");
        }
        MetodoEnvio metodoGuardado = metodoEnvioRepository.save(metodoEnvio);
        return convertirADTO(metodoGuardado);
    }

    public MetodoEnvioDTO actualizarMetodoEnvio(Integer id, MetodoEnvio datosActualizados) {
        MetodoEnvio metodoExistente = metodoEnvioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metodo de envio no encontrado con ID: " + id));

        if (datosActualizados.getFechaSalida() != null) {
            metodoExistente.setFechaSalida(datosActualizados.getFechaSalida());
        }
        if (datosActualizados.getFechaEntrega() != null) {
            metodoExistente.setFechaEntrega(datosActualizados.getFechaEntrega());
        }
        metodoExistente.setTipoEnvio(datosActualizados.getTipoEnvio());
        metodoExistente.setSucursal(datosActualizados.getSucursal());

        MetodoEnvio metodoActualizado = metodoEnvioRepository.save(metodoExistente);
        return convertirADTO(metodoActualizado);
    }

    public void eliminarMetodoEnvio(Integer id) {
        if (!metodoEnvioRepository.existsById(id)) {
            throw new RuntimeException("Metodo de envio no encontrado con ID: " + id);
        }
        metodoEnvioRepository.deleteById(id);
    }
}