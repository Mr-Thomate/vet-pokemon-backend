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
import vet_pokemon.vet_backend.assembler.ProductoModelAssembler;
import vet_pokemon.vet_backend.dto.ProductoDTO;
import vet_pokemon.vet_backend.model.Producto;
import vet_pokemon.vet_backend.service.ProductoService;

@Tag(name = "Productos", description = "Operaciones CRUD para productos")
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler assembler;

    @Operation(summary = "Listar todos los productos", description = "Retorna la lista completa de productos registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen productos registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ProductoDTO>>> obtenerTodos() {
        List<EntityModel<ProductoDTO>> lista = productoService.listarProductos().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ProductoController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna los datos del producto indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ProductoDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            ProductoDTO dto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o producto ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ProductoDTO>> crear(@Valid @RequestBody Producto nuevo) {
        try {
            ProductoDTO guardado = productoService.guardarProducto(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ProductoDTO dto = productoService.obtenerProductoPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(ProductoController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar producto", description = "Modifica los datos del producto indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ProductoDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Producto datos) {
        try {
            productoService.actualizarProducto(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ProductoDTO dto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar producto", description = "Elimina de la base de datos el producto indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
