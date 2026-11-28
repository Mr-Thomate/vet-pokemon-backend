package vet_pokemon.vet_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.VentaDTO;
import vet_pokemon.vet_backend.model.Usuario;
import vet_pokemon.vet_backend.model.Venta;
import vet_pokemon.vet_backend.repository.VentaRepository;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    private VentaDTO convertirADTO(Venta venta) {
        VentaDTO dto = new VentaDTO();

        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());

        if (venta.getUsuario() != null) {
            Usuario u = venta.getUsuario();
            String nombreCompleto = u.getPnombre();
            if (u.getSnombre() != null && !u.getSnombre().isBlank()) {
                nombreCompleto += " " + u.getSnombre();
            }
            nombreCompleto += " " + u.getPapellido();
            if (u.getSapellido() != null && !u.getSapellido().isBlank()) {
                nombreCompleto += " " + u.getSapellido();
            }
            dto.setNombreUsuario(nombreCompleto.trim());
        }

        if (venta.getEstado() != null) {
            dto.setNombreEstado(venta.getEstado().getDescripcion());
        }

        if (venta.getMetodoPago() != null) {
            dto.setNombreMetodoPago(venta.getMetodoPago().getNombremetodopago());
        }

        if (venta.getMetodoEnvio() != null) {
            dto.setNombreMetodoEnvio(venta.getMetodoEnvio().getTipoEnvio());
        }

        dto.setDetalles(new ArrayList<>());

        return dto;
    }

    public List<VentaDTO> listarVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        List<VentaDTO> listaDTO = new ArrayList<>();

        for (Venta venta : ventas) {
            listaDTO.add(convertirADTO(venta));
        }
        return listaDTO;
    }

    public VentaDTO obtenerVentaPorId(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        return convertirADTO(venta);
    }

    public List<VentaDTO> listarPorUsuario(Integer idUsuario) {
        List<Venta> ventas = ventaRepository.findByIdUsuario(idUsuario);
        List<VentaDTO> listaDTO = new ArrayList<>();

        for (Venta venta : ventas) {
            listaDTO.add(convertirADTO(venta));
        }
        return listaDTO;
    }

    public List<VentaDTO> listarPorEstado(Integer idEstado) {
        List<Venta> ventas = ventaRepository.findByIdEstado(idEstado);
        List<VentaDTO> listaDTO = new ArrayList<>();

        for (Venta venta : ventas) {
            listaDTO.add(convertirADTO(venta));
        }
        return listaDTO;
    }

    public VentaDTO guardarVenta(Venta venta) {
        if (venta.getFecha() == null) {
            venta.setFecha(LocalDateTime.now());
        }
        Venta ventaGuardada = ventaRepository.save(venta);
        return convertirADTO(ventaGuardada);
    }

    public VentaDTO actualizarVenta(Integer id, Venta datosActualizados) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        if (datosActualizados.getFecha() != null) {
            ventaExistente.setFecha(datosActualizados.getFecha());
        }
        if (datosActualizados.getTotal() != null) {
            ventaExistente.setTotal(datosActualizados.getTotal());
        }
        if (datosActualizados.getUsuario() != null) {
            ventaExistente.setUsuario(datosActualizados.getUsuario());
        }
        if (datosActualizados.getMetodoPago() != null) {
            ventaExistente.setMetodoPago(datosActualizados.getMetodoPago());
        }
        if (datosActualizados.getMetodoEnvio() != null) {
            ventaExistente.setMetodoEnvio(datosActualizados.getMetodoEnvio());
        }
        if (datosActualizados.getEstado() != null) {
            ventaExistente.setEstado(datosActualizados.getEstado());
        }

        Venta ventaActualizada = ventaRepository.save(ventaExistente);
        return convertirADTO(ventaActualizada);
    }

    public void eliminarVenta(Integer id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
        ventaRepository.deleteById(id);
    }
}