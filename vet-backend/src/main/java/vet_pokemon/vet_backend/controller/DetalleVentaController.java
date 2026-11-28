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
import vet_pokemon.vet_backend.assembler.DetalleVentaModelAssembler;
import vet_pokemon.vet_backend.dto.DetalleVentaDTO;
import vet_pokemon.vet_backend.model.DetalleVenta;
import vet_pokemon.vet_backend.service.DetalleVentaService;

@Tag(name = "Detalles de venta", description = "Operaciones CRUD para detalles de venta")
@RestController
@RequestMapping("/api/v1/detalles-venta")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private DetalleVentaModelAssembler assembler;

    @Operation(summary = "Listar todos los detalles de venta", description = "Retorna la lista completa de detalles de venta registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = DetalleVentaDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen detalles de venta registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<DetalleVentaDTO>>> obtenerTodos() {
        List<EntityModel<DetalleVentaDTO>> lista = detalleVentaService.listarDetallesVenta().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(DetalleVentaController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener detalle de venta por ID", description = "Retorna los datos del detalle de venta indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle de venta encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = DetalleVentaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<DetalleVentaDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            DetalleVentaDTO dto = detalleVentaService.obtenerDetalleVentaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar detalles de una venta", description = "Retorna los detalles asociados a la venta indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalles de la venta encontrados", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = DetalleVentaDTO.class))),
        @ApiResponse(responseCode = "204", description = "La venta no tiene detalles", content = @Content)
    })
    @GetMapping(value = "/venta/{idVenta}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<DetalleVentaDTO>>> listarPorVenta(@PathVariable Integer idVenta) {
        List<EntityModel<DetalleVentaDTO>> lista = detalleVentaService.listarPorVenta(idVenta).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(DetalleVentaController.class).listarPorVenta(idVenta)).withSelfRel()));
    }

    @Operation(summary = "Crear detalle de venta", description = "Registra un nuevo detalle de venta en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Detalle de venta creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = DetalleVentaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o detalle de venta ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<DetalleVentaDTO>> crear(@Valid @RequestBody DetalleVenta nuevo) {
        try {
            DetalleVentaDTO guardado = detalleVentaService.guardarDetalleVenta(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            DetalleVentaDTO dto = detalleVentaService.obtenerDetalleVentaPorId(guardado.getId_DetalleVenta());
            return ResponseEntity
                    .created(linkTo(methodOn(DetalleVentaController.class).obtenerPorId(dto.getId_DetalleVenta())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar detalle de venta", description = "Modifica los datos del detalle de venta indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle de venta actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = DetalleVentaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<DetalleVentaDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody DetalleVenta datos) {
        try {
            detalleVentaService.actualizarDetalleVenta(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            DetalleVentaDTO dto = detalleVentaService.obtenerDetalleVentaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar detalle de venta", description = "Elimina de la base de datos el detalle de venta indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Detalle de venta eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            detalleVentaService.eliminarDetalleVenta(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
