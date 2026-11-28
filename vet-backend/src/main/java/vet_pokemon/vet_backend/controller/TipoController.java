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
import vet_pokemon.vet_backend.assembler.TipoModelAssembler;
import vet_pokemon.vet_backend.dto.TipoDTO;
import vet_pokemon.vet_backend.model.Tipo;
import vet_pokemon.vet_backend.service.TipoService;

@Tag(name = "Tipos", description = "Operaciones CRUD para tipos")
@RestController
@RequestMapping("/api/v1/tipos")
public class TipoController {

    @Autowired
    private TipoService tipoService;

    @Autowired
    private TipoModelAssembler assembler;

    @Operation(summary = "Listar todos los tipos", description = "Retorna la lista completa de tipos registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = TipoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen tipos registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<TipoDTO>>> obtenerTodos() {
        List<EntityModel<TipoDTO>> lista = tipoService.listarTipos().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(TipoController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener tipo por ID", description = "Retorna los datos del tipo indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = TipoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Tipo no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<TipoDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            TipoDTO dto = tipoService.obtenerTipoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear tipo", description = "Registra un nuevo tipo en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tipo creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = TipoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o tipo ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<TipoDTO>> crear(@Valid @RequestBody Tipo nuevo) {
        try {
            TipoDTO guardado = tipoService.guardarTipo(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            TipoDTO dto = tipoService.obtenerTipoPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(TipoController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar tipo", description = "Modifica los datos del tipo indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tipo actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = TipoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tipo no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<TipoDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Tipo datos) {
        try {
            tipoService.actualizarTipo(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            TipoDTO dto = tipoService.obtenerTipoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar tipo", description = "Elimina de la base de datos el tipo indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tipo eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tipo no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            tipoService.eliminarTipo(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
