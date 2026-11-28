package vet_pokemon.vet_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.DetalleVentaDTO;
import vet_pokemon.vet_backend.model.DetalleVenta;
import vet_pokemon.vet_backend.repository.DetalleVentaRepository;

@Service
@Transactional
public class DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    private DetalleVentaDTO convertirADTO(DetalleVenta detalle) {
        DetalleVentaDTO dto = new DetalleVentaDTO();

        dto.setId_DetalleVenta(detalle.getId_DetalleVenta());
        dto.setDescripcion(detalle.getDescripcion());
        dto.setFechaVenta(detalle.getFechaVenta());
        dto.setTotalVenta(detalle.getTotalVenta());
        dto.setSucursal(detalle.getSucursal());

        return dto;
    }

    public List<DetalleVentaDTO> listarDetallesVenta() {
        List<DetalleVenta> detalles = detalleVentaRepository.findAll();
        List<DetalleVentaDTO> listaDTO = new ArrayList<>();

        for (DetalleVenta detalle : detalles) {
            listaDTO.add(convertirADTO(detalle));
        }
        return listaDTO;
    }

    public DetalleVentaDTO obtenerDetalleVentaPorId(Integer id) {
        DetalleVenta detalle = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado con ID: " + id));
        return convertirADTO(detalle);
    }

    public List<DetalleVentaDTO> listarPorVenta(Integer idVenta) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByIdVenta(idVenta);
        List<DetalleVentaDTO> listaDTO = new ArrayList<>();

        for (DetalleVenta detalle : detalles) {
            listaDTO.add(convertirADTO(detalle));
        }
        return listaDTO;
    }

    public DetalleVentaDTO guardarDetalleVenta(DetalleVenta detalle) {
        if (detalle.getFechaVenta() == null) {
            detalle.setFechaVenta(LocalDate.now());
        }
        DetalleVenta detalleGuardado = detalleVentaRepository.save(detalle);
        return convertirADTO(detalleGuardado);
    }

    public DetalleVentaDTO actualizarDetalleVenta(Integer id, DetalleVenta datosActualizados) {
        DetalleVenta detalleExistente = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado con ID: " + id));

        if (datosActualizados.getDescripcion() != null) {
            detalleExistente.setDescripcion(datosActualizados.getDescripcion());
        }
        if (datosActualizados.getSucursal() != null) {
            detalleExistente.setSucursal(datosActualizados.getSucursal());
        }
        if (datosActualizados.getFechaVenta() != null) {
            detalleExistente.setFechaVenta(datosActualizados.getFechaVenta());
        }
        if (datosActualizados.getTotalVenta() != null) {
            detalleExistente.setTotalVenta(datosActualizados.getTotalVenta());
        }
        if (datosActualizados.getVenta() != null) {
            detalleExistente.setVenta(datosActualizados.getVenta());
        }

        DetalleVenta detalleActualizado = detalleVentaRepository.save(detalleExistente);
        return convertirADTO(detalleActualizado);
    }

    public void eliminarDetalleVenta(Integer id) {
        if (!detalleVentaRepository.existsById(id)) {
            throw new RuntimeException("Detalle de venta no encontrado con ID: " + id);
        }
        detalleVentaRepository.deleteById(id);
    }
}