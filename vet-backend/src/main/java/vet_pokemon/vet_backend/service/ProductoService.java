package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.ProductoDTO;
import vet_pokemon.vet_backend.model.Producto;
import vet_pokemon.vet_backend.repository.ProductoRepository;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();

        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());

        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }

        if (producto.getTipo() != null) {
            dto.setNombreTipo(producto.getTipo().getNombre());
        }

        dto.setImagenes(new ArrayList<>());

        return dto;
    }

    public List<ProductoDTO> listarProductos() {
        List<Producto> productos = productoRepository.findAll();
        List<ProductoDTO> listaDTO = new ArrayList<>();

        for (Producto prod : productos) {
            listaDTO.add(convertirADTO(prod));
        }
        return listaDTO;
    }

    public ProductoDTO obtenerProductoPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    public ProductoDTO guardarProducto(Producto producto) {
        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new RuntimeException("El producto ya se encuentra registrado");
        }
        Producto productoGuardado = productoRepository.save(producto);
        return convertirADTO(productoGuardado);
    }

    public ProductoDTO actualizarProducto(Integer id, Producto datosActualizados) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        productoExistente.setNombre(datosActualizados.getNombre());
        productoExistente.setDescripcion(datosActualizados.getDescripcion());
        productoExistente.setPrecio(datosActualizados.getPrecio());
        productoExistente.setStock(datosActualizados.getStock());

        if (datosActualizados.getCategoria() != null) {
            productoExistente.setCategoria(datosActualizados.getCategoria());
        }
        if (datosActualizados.getTipo() != null) {
            productoExistente.setTipo(datosActualizados.getTipo());
        }

        Producto productoActualizado = productoRepository.save(productoExistente);
        return convertirADTO(productoActualizado);
    }

    public void eliminarProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}