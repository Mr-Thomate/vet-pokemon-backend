package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.CategoriaDTO;
import vet_pokemon.vet_backend.model.Categoria;
import vet_pokemon.vet_backend.repository.CategoriaRepository;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();

        dto.setId(categoria.getId());
        dto.setNombrecategoria(categoria.getNombre());

        return dto;
    }

    public List<CategoriaDTO> listarCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<CategoriaDTO> listaDTO = new ArrayList<>();

        for (Categoria categoria : categorias) {
            listaDTO.add(convertirADTO(categoria));
        }
        return listaDTO;
    }

    public CategoriaDTO obtenerCategoriaPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));
        return convertirADTO(categoria);
    }

    public CategoriaDTO guardarCategoria(Categoria categoria) {
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new RuntimeException("La categoria ya se encuentra registrada");
        }
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return convertirADTO(categoriaGuardada);
    }

    public CategoriaDTO actualizarCategoria(Integer id, Categoria datosActualizados) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));

        categoriaExistente.setNombre(datosActualizados.getNombre());

        Categoria categoriaActualizada = categoriaRepository.save(categoriaExistente);
        return convertirADTO(categoriaActualizada);
    }

    public void eliminarCategoria(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}