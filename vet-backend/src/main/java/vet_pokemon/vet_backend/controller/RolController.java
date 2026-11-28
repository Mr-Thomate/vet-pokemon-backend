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
import vet_pokemon.vet_backend.assembler.RolModelAssembler;
import vet_pokemon.vet_backend.dto.RolDTO;
import vet_pokemon.vet_backend.model.Rol;
import vet_pokemon.vet_backend.service.RolService;

@Tag(name = "Roles", description = "Operaciones CRUD para roles")
@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Autowired
    private RolModelAssembler assembler;

    @Operation(summary = "Listar todos los roles", description = "Retorna la lista completa de roles registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RolDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen roles registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<RolDTO>>> obtenerTodos() {
        List<EntityModel<RolDTO>> lista = rolService.listarRoles().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(RolController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener rol por ID", description = "Retorna los datos del rol indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RolDTO.class))),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RolDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            RolDTO dto = rolService.obtenerRolPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear rol", description = "Registra un nuevo rol en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Rol creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RolDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o rol ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RolDTO>> crear(@Valid @RequestBody Rol nuevo) {
        try {
            RolDTO guardado = rolService.guardarRol(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            RolDTO dto = rolService.obtenerRolPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(RolController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar rol", description = "Modifica los datos del rol indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RolDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RolDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Rol datos) {
        try {
            rolService.actualizarRol(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            RolDTO dto = rolService.obtenerRolPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar rol", description = "Elimina de la base de datos el rol indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Rol eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            rolService.eliminarRol(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
