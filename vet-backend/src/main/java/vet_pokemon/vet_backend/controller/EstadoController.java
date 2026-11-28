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
import vet_pokemon.vet_backend.assembler.EstadoModelAssembler;
import vet_pokemon.vet_backend.dto.EstadoDTO;
import vet_pokemon.vet_backend.model.Estado;
import vet_pokemon.vet_backend.service.EstadoService;

@Tag(name = "Estados", description = "Operaciones CRUD para estados")
@RestController
@RequestMapping("/api/v1/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @Autowired
    private EstadoModelAssembler assembler;

    @Operation(summary = "Listar todos los estados", description = "Retorna la lista completa de estados registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = EstadoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen estados registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<EstadoDTO>>> obtenerTodos() {
        List<EntityModel<EstadoDTO>> lista = estadoService.listarEstados().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(EstadoController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener estado por ID", description = "Retorna los datos del estado indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = EstadoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<EstadoDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            EstadoDTO dto = estadoService.obtenerEstadoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar estados por descripción", description = "Retorna los estados cuya descripción coincide con el valor indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estados encontrados", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = EstadoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No se encontraron estados con esa descripción", content = @Content)
    })
    @GetMapping(value = "/descripcion/{descripcion}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<EstadoDTO>>> buscarPorDescripcion(@PathVariable String descripcion) {
        List<EntityModel<EstadoDTO>> lista = estadoService.buscarPorDescripcion(descripcion).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(EstadoController.class).buscarPorDescripcion(descripcion)).withSelfRel()));
    }

    @Operation(summary = "Crear estado", description = "Registra un nuevo estado en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Estado creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = EstadoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o estado ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<EstadoDTO>> crear(@Valid @RequestBody Estado nuevo) {
        try {
            EstadoDTO guardado = estadoService.guardarEstado(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            EstadoDTO dto = estadoService.obtenerEstadoPorId(guardado.getId_estado());
            return ResponseEntity
                    .created(linkTo(methodOn(EstadoController.class).obtenerPorId(dto.getId_estado())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar estado", description = "Modifica los datos del estado indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = EstadoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<EstadoDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Estado datos) {
        try {
            estadoService.actualizarEstado(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            EstadoDTO dto = estadoService.obtenerEstadoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar estado", description = "Elimina de la base de datos el estado indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estado eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Estado no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            estadoService.eliminarEstado(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
