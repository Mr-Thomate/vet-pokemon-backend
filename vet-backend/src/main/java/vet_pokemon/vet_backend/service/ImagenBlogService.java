package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.ImagenBlogDTO;
import vet_pokemon.vet_backend.model.ImagenBlog;
import vet_pokemon.vet_backend.repository.ImagenBlogRepository;

@Service
@Transactional
public class ImagenBlogService {

    @Autowired
    private ImagenBlogRepository imagenBlogRepository;

    private ImagenBlogDTO convertirADTO(ImagenBlog imagen) {
        ImagenBlogDTO dto = new ImagenBlogDTO();

        dto.setId(imagen.getId());
        dto.setUrl(imagen.getUrl());

        return dto;
    }

    public List<ImagenBlogDTO> listarImagenesBlog() {
        List<ImagenBlog> imagenes = imagenBlogRepository.findAll();
        List<ImagenBlogDTO> listaDTO = new ArrayList<>();

        for (ImagenBlog imagen : imagenes) {
            listaDTO.add(convertirADTO(imagen));
        }
        return listaDTO;
    }

    public ImagenBlogDTO obtenerImagenBlogPorId(Integer id) {
        ImagenBlog imagen = imagenBlogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen de blog no encontrada con ID: " + id));
        return convertirADTO(imagen);
    }

    public List<ImagenBlogDTO> listarPorBlog(Integer idBlog) {
        List<ImagenBlog> imagenes = imagenBlogRepository.findByIdBlog(idBlog);
        List<ImagenBlogDTO> listaDTO = new ArrayList<>();

        for (ImagenBlog imagen : imagenes) {
            listaDTO.add(convertirADTO(imagen));
        }
        return listaDTO;
    }

    public ImagenBlogDTO guardarImagenBlog(ImagenBlog imagen) {
        ImagenBlog imagenGuardada = imagenBlogRepository.save(imagen);
        return convertirADTO(imagenGuardada);
    }

    public ImagenBlogDTO actualizarImagenBlog(Integer id, ImagenBlog datosActualizados) {
        ImagenBlog imagenExistente = imagenBlogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen de blog no encontrada con ID: " + id));

        imagenExistente.setUrl(datosActualizados.getUrl());

        if (datosActualizados.getBlog() != null) {
            imagenExistente.setBlog(datosActualizados.getBlog());
        }

        ImagenBlog imagenActualizada = imagenBlogRepository.save(imagenExistente);
        return convertirADTO(imagenActualizada);
    }

    public void eliminarImagenBlog(Integer id) {
        if (!imagenBlogRepository.existsById(id)) {
            throw new RuntimeException("Imagen de blog no encontrada con ID: " + id);
        }
        imagenBlogRepository.deleteById(id);
    }
}