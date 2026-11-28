package vet_pokemon.vet_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vet_pokemon.vet_backend.assembler.CategoriaModelAssembler;
import vet_pokemon.vet_backend.dto.CategoriaDTO;
import vet_pokemon.vet_backend.model.Categoria;
import vet_pokemon.vet_backend.service.CategoriaService;

@Tag(name = "Categorías", description = "Operaciones CRUD para categorías")
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaModelAssembler assembler;

    @Operation(summary = "Listar todas las categorías", description = "Retorna la lista completa de categorías registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen categorías registradas", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<CategoriaDTO>>> obtenerTodos() {
        List<EntityModel<CategoriaDTO>> lista = categoriaService.listarCategorias().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(CategoriaController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener categoría por ID", description = "Retorna los datos de la categoría indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CategoriaDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            CategoriaDTO dto = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear categoría", description = "Registra una nueva categoría en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o categoría ya registrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CategoriaDTO>> crear(@Valid @RequestBody Categoria nueva) {
        try {
            CategoriaDTO guardado = categoriaService.guardarCategoria(nueva);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            CategoriaDTO dto = categoriaService.obtenerCategoriaPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(CategoriaController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar categoría", description = "Modifica los datos de la categoría indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = CategoriaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CategoriaDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Categoria datos) {
        try {
            categoriaService.actualizarCategoria(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            CategoriaDTO dto = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina de la base de datos la categoría indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usada por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
