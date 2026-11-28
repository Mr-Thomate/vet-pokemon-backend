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
import vet_pokemon.vet_backend.assembler.ImagenProductoModelAssembler;
import vet_pokemon.vet_backend.dto.ImagenProductoDTO;
import vet_pokemon.vet_backend.model.ImagenProducto;
import vet_pokemon.vet_backend.service.ImagenProductoService;

@Tag(name = "Imágenes de producto", description = "Operaciones CRUD para imágenes de producto")
@RestController
@RequestMapping("/api/v1/imagenes-producto")
public class ImagenProductoController {

    @Autowired
    private ImagenProductoService imagenProductoService;

    @Autowired
    private ImagenProductoModelAssembler assembler;

    @Operation(summary = "Listar todas las imágenes de producto", description = "Retorna la lista completa de imágenes de producto registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenProductoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen imágenes de producto registradas", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ImagenProductoDTO>>> obtenerTodos() {
        List<EntityModel<ImagenProductoDTO>> lista = imagenProductoService.listarImagenesProducto().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ImagenProductoController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener imagen de producto por ID", description = "Retorna los datos de la imagen de producto indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen de producto encontrada", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Imagen de producto no encontrada con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ImagenProductoDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            ImagenProductoDTO dto = imagenProductoService.obtenerImagenProductoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar imágenes de un producto", description = "Retorna las imágenes asociadas al producto indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imágenes del producto encontradas", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenProductoDTO.class))),
        @ApiResponse(responseCode = "204", description = "El producto no tiene imágenes", content = @Content)
    })
    @GetMapping(value = "/producto/{idProducto}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ImagenProductoDTO>>> listarPorProducto(@PathVariable Integer idProducto) {
        List<EntityModel<ImagenProductoDTO>> lista = imagenProductoService.listarPorProducto(idProducto).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ImagenProductoController.class).listarPorProducto(idProducto)).withSelfRel()));
    }

    @Operation(summary = "Crear imagen de producto", description = "Registra una nueva imagen de producto en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen de producto creada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenProductoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o imagen de producto ya registrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ImagenProductoDTO>> crear(@Valid @RequestBody ImagenProducto nueva) {
        try {
            ImagenProductoDTO guardado = imagenProductoService.guardarImagenProducto(nueva);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ImagenProductoDTO dto = imagenProductoService.obtenerImagenProductoPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(ImagenProductoController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar imagen de producto", description = "Modifica los datos de la imagen de producto indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen de producto actualizada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ImagenProductoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Imagen de producto no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ImagenProductoDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody ImagenProducto datos) {
        try {
            imagenProductoService.actualizarImagenProducto(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ImagenProductoDTO dto = imagenProductoService.obtenerImagenProductoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar imagen de producto", description = "Elimina de la base de datos la imagen de producto indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagen de producto eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Imagen de producto no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usada por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            imagenProductoService.eliminarImagenProducto(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
