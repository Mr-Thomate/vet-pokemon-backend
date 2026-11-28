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
import vet_pokemon.vet_backend.assembler.ImagenBlogModelAssembler;
import vet_pokemon.vet_backend.dto.ImagenBlogDTO;
import vet_pokemon.vet_backend.model.ImagenBlog;
import vet_pokemon.vet_backend.service.ImagenBlogService;

@Tag(name = "Imágenes de blog", description = "Operaciones CRUD para imágenes de blog")
@RestController
@RequestMapping("/api/v1/imagenes-blog")
public class ImagenBlogController {

    @Autowired
    private ImagenBlogService imagenBlogService;

    @Autowired
    private ImagenBlogModelAssembler assembler;

    @Operation(summary = "Listar todas las imágenes de blog", description = "Retorna la lista completa de imágenes de blog registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenBlogDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen imágenes de blog registradas", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ImagenBlogDTO>>> obtenerTodos() {
        List<EntityModel<ImagenBlogDTO>> lista = imagenBlogService.listarImagenesBlog().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ImagenBlogController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener imagen de blog por ID", description = "Retorna los datos de la imagen de blog indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen de blog encontrada", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenBlogDTO.class))),
        @ApiResponse(responseCode = "404", description = "Imagen de blog no encontrada con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ImagenBlogDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            ImagenBlogDTO dto = imagenBlogService.obtenerImagenBlogPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar imágenes de un blog", description = "Retorna las imágenes asociadas al blog indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imágenes del blog encontradas", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenBlogDTO.class))),
        @ApiResponse(responseCode = "204", description = "El blog no tiene imágenes", content = @Content)
    })
    @GetMapping(value = "/blog/{idBlog}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ImagenBlogDTO>>> listarPorBlog(@PathVariable Integer idBlog) {
        List<EntityModel<ImagenBlogDTO>> lista = imagenBlogService.listarPorBlog(idBlog).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ImagenBlogController.class).listarPorBlog(idBlog)).withSelfRel()));
    }

    @Operation(summary = "Crear imagen de blog", description = "Registra una nueva imagen de blog en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen de blog creada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenBlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o imagen de blog ya registrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ImagenBlogDTO>> crear(@Valid @RequestBody ImagenBlog nueva) {
        try {
            ImagenBlogDTO guardado = imagenBlogService.guardarImagenBlog(nueva);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ImagenBlogDTO dto = imagenBlogService.obtenerImagenBlogPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(ImagenBlogController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar imagen de blog", description = "Modifica los datos de la imagen de blog indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen de blog actualizada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenBlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Imagen de blog no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ImagenBlogDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody ImagenBlog datos) {
        try {
            imagenBlogService.actualizarImagenBlog(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ImagenBlogDTO dto = imagenBlogService.obtenerImagenBlogPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar imagen de blog", description = "Elimina de la base de datos la imagen de blog indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagen de blog eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Imagen de blog no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usada por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            imagenBlogService.eliminarImagenBlog(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
