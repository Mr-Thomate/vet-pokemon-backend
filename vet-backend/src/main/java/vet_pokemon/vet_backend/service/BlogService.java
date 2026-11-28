package vet_pokemon.vet_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.BlogDTO;
import vet_pokemon.vet_backend.model.Blog;
import vet_pokemon.vet_backend.model.ImagenBlog;
import vet_pokemon.vet_backend.repository.BlogRepository;

@Service
@Transactional
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    private BlogDTO convertirADTO(Blog blog) {
        BlogDTO dto = new BlogDTO();

        dto.setId(blog.getId());
        dto.setTitulo(blog.getTitulo());
        dto.setContenido(blog.getContenido());
        dto.setFechaPublicacion(blog.getFechaPublicacion());

        List<String> urlsImagenes = new ArrayList<>();
        if (blog.getImagenes() != null) {
            for (ImagenBlog img : blog.getImagenes()) {
                urlsImagenes.add(img.getUrl());
            }
        }
        dto.setImagenes(urlsImagenes);

        return dto;
    }

    public List<BlogDTO> listarBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        List<BlogDTO> listaDTO = new ArrayList<>();

        for (Blog blog : blogs) {
            listaDTO.add(convertirADTO(blog));
        }
        return listaDTO;
    }

    public BlogDTO obtenerBlogPorId(Integer id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog no encontrado con ID: " + id));
        return convertirADTO(blog);
    }

    public List<BlogDTO> buscarPorTitulo(String titulo) {
        List<Blog> blogs = blogRepository.findByTituloContainingIgnoreCase(titulo);
        List<BlogDTO> listaDTO = new ArrayList<>();

        for (Blog blog : blogs) {
            listaDTO.add(convertirADTO(blog));
        }
        return listaDTO;
    }

    public BlogDTO guardarBlog(Blog blog) {
        if (blogRepository.existsByTituloIgnoreCase(blog.getTitulo())) {
            throw new RuntimeException("Ya existe un blog con ese titulo");
        }
        if (blog.getFechaPublicacion() == null) {
            blog.setFechaPublicacion(LocalDateTime.now());
        }
        Blog blogGuardado = blogRepository.save(blog);
        return convertirADTO(blogGuardado);
    }

    public BlogDTO actualizarBlog(Integer id, Blog datosActualizados) {
        Blog blogExistente = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog no encontrado con ID: " + id));

        blogExistente.setTitulo(datosActualizados.getTitulo());
        blogExistente.setContenido(datosActualizados.getContenido());

        if (datosActualizados.getFechaPublicacion() != null) {
            blogExistente.setFechaPublicacion(datosActualizados.getFechaPublicacion());
        }

        Blog blogActualizado = blogRepository.save(blogExistente);
        return convertirADTO(blogActualizado);
    }

    public void eliminarBlog(Integer id) {
        if (!blogRepository.existsById(id)) {
            throw new RuntimeException("Blog no encontrado con ID: " + id);
        }
        blogRepository.deleteById(id);
    }
}