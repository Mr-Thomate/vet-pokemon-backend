package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.ImagenProductoDTO;
import vet_pokemon.vet_backend.model.ImagenProducto;
import vet_pokemon.vet_backend.repository.ImagenProductoRepository;

@Service
@Transactional
public class ImagenProductoService {

    @Autowired
    private ImagenProductoRepository imagenProductoRepository;

    private ImagenProductoDTO convertirADTO(ImagenProducto imagen) {
        ImagenProductoDTO dto = new ImagenProductoDTO();

        dto.setId(imagen.getId());
        dto.setUrl(imagen.getUrl());

        return dto;
    }

    public List<ImagenProductoDTO> listarImagenesProducto() {
        List<ImagenProducto> imagenes = imagenProductoRepository.findAll();
        List<ImagenProductoDTO> listaDTO = new ArrayList<>();

        for (ImagenProducto imagen : imagenes) {
            listaDTO.add(convertirADTO(imagen));
        }
        return listaDTO;
    }

    public ImagenProductoDTO obtenerImagenProductoPorId(Integer id) {
        ImagenProducto imagen = imagenProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen de producto no encontrada con ID: " + id));
        return convertirADTO(imagen);
    }

    public List<ImagenProductoDTO> listarPorProducto(Integer idProducto) {
        List<ImagenProducto> imagenes = imagenProductoRepository.findByIdProducto(idProducto);
        List<ImagenProductoDTO> listaDTO = new ArrayList<>();

        for (ImagenProducto imagen : imagenes) {
            listaDTO.add(convertirADTO(imagen));
        }
        return listaDTO;
    }

    public ImagenProductoDTO guardarImagenProducto(ImagenProducto imagen) {
        ImagenProducto imagenGuardada = imagenProductoRepository.save(imagen);
        return convertirADTO(imagenGuardada);
    }

    public ImagenProductoDTO actualizarImagenProducto(Integer id, ImagenProducto datosActualizados) {
        ImagenProducto imagenExistente = imagenProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen de producto no encontrada con ID: " + id));

        imagenExistente.setUrl(datosActualizados.getUrl());

        if (datosActualizados.getProducto() != null) {
            imagenExistente.setProducto(datosActualizados.getProducto());
        }

        ImagenProducto imagenActualizada = imagenProductoRepository.save(imagenExistente);
        return convertirADTO(imagenActualizada);
    }

    public void eliminarImagenProducto(Integer id) {
        if (!imagenProductoRepository.existsById(id)) {
            throw new RuntimeException("Imagen de producto no encontrada con ID: " + id);
        }
        imagenProductoRepository.deleteById(id);
    }
}