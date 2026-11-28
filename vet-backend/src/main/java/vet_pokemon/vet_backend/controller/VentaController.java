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
import vet_pokemon.vet_backend.assembler.VentaModelAssembler;
import vet_pokemon.vet_backend.dto.VentaDTO;
import vet_pokemon.vet_backend.model.Venta;
import vet_pokemon.vet_backend.service.VentaService;

@Tag(name = "Ventas", description = "Operaciones CRUD para ventas")
@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaModelAssembler assembler;

    @Operation(summary = "Listar todas las ventas", description = "Retorna la lista completa de ventas registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen ventas registradas", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> obtenerTodos() {
        List<EntityModel<VentaDTO>> lista = ventaService.listarVentas().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(VentaController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener venta por ID", description = "Retorna los datos de la venta indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<VentaDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            VentaDTO dto = ventaService.obtenerVentaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar ventas de un usuario", description = "Retorna las ventas realizadas por el usuario indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ventas del usuario encontradas", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene ventas", content = @Content)
    })
    @GetMapping(value = "/usuario/{idUsuario}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> listarPorUsuario(@PathVariable Integer idUsuario) {
        List<EntityModel<VentaDTO>> lista = ventaService.listarPorUsuario(idUsuario).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(VentaController.class).listarPorUsuario(idUsuario)).withSelfRel()));
    }

    @Operation(summary = "Listar ventas por estado", description = "Retorna las ventas que se encuentran en el estado indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ventas con ese estado encontradas", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "204", description = "No hay ventas con ese estado", content = @Content)
    })
    @GetMapping(value = "/estado/{idEstado}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> listarPorEstado(@PathVariable Integer idEstado) {
        List<EntityModel<VentaDTO>> lista = ventaService.listarPorEstado(idEstado).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(VentaController.class).listarPorEstado(idEstado)).withSelfRel()));
    }

    @Operation(summary = "Crear venta", description = "Registra una nueva venta en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Venta creada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o venta ya registrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<VentaDTO>> crear(@Valid @RequestBody Venta nueva) {
        try {
            VentaDTO guardado = ventaService.guardarVenta(nueva);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            VentaDTO dto = ventaService.obtenerVentaPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(VentaController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar venta", description = "Modifica los datos de la venta indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta actualizada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = VentaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<VentaDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Venta datos) {
        try {
            ventaService.actualizarVenta(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            VentaDTO dto = ventaService.obtenerVentaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar venta", description = "Elimina de la base de datos la venta indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Venta eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usada por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
