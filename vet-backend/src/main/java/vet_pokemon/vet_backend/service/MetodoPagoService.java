package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.MetodoPagoDTO;
import vet_pokemon.vet_backend.model.MetodoPago;
import vet_pokemon.vet_backend.repository.MetodoPagoRepository;

@Service
@Transactional
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    private MetodoPagoDTO convertirADTO(MetodoPago metodoPago) {
        MetodoPagoDTO dto = new MetodoPagoDTO();

        dto.setId_MetodoPago(metodoPago.getId_MetodoPago());
        dto.setDescripcion(metodoPago.getDescripcion());
        dto.setNombremetodopago(metodoPago.getNombremetodopago());
        dto.setMonto_total(metodoPago.getMonto_total());

        return dto;
    }

    public List<MetodoPagoDTO> listarMetodosPago() {
        List<MetodoPago> metodos = metodoPagoRepository.findAll();
        List<MetodoPagoDTO> listaDTO = new ArrayList<>();

        for (MetodoPago metodo : metodos) {
            listaDTO.add(convertirADTO(metodo));
        }
        return listaDTO;
    }

    public MetodoPagoDTO obtenerMetodoPagoPorId(Integer id) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metodo de pago no encontrado con ID: " + id));
        return convertirADTO(metodoPago);
    }

    public List<MetodoPagoDTO> buscarPorNombre(String nombre) {
        List<MetodoPago> metodos = metodoPagoRepository.findByNombre(nombre);
        List<MetodoPagoDTO> listaDTO = new ArrayList<>();

        for (MetodoPago metodo : metodos) {
            listaDTO.add(convertirADTO(metodo));
        }
        return listaDTO;
    }

    public MetodoPagoDTO guardarMetodoPago(MetodoPago metodoPago) {
        if (!metodoPagoRepository.findByNombre(metodoPago.getNombremetodopago()).isEmpty()) {
            throw new RuntimeException("El metodo de pago ya se encuentra registrado");
        }
        MetodoPago metodoGuardado = metodoPagoRepository.save(metodoPago);
        return convertirADTO(metodoGuardado);
    }

    public MetodoPagoDTO actualizarMetodoPago(Integer id, MetodoPago datosActualizados) {
        MetodoPago metodoExistente = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Metodo de pago no encontrado con ID: " + id));

        metodoExistente.setDescripcion(datosActualizados.getDescripcion());
        metodoExistente.setNombremetodopago(datosActualizados.getNombremetodopago());
        metodoExistente.setMonto_total(datosActualizados.getMonto_total());

        MetodoPago metodoActualizado = metodoPagoRepository.save(metodoExistente);
        return convertirADTO(metodoActualizado);
    }

    public void eliminarMetodoPago(Integer id) {
        if (!metodoPagoRepository.existsById(id)) {
            throw new RuntimeException("Metodo de pago no encontrado con ID: " + id);
        }
        metodoPagoRepository.deleteById(id);
    }
}