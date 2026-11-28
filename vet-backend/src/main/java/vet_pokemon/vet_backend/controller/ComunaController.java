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
import vet_pokemon.vet_backend.assembler.ComunaModelAssembler;
import vet_pokemon.vet_backend.dto.ComunaDTO;
import vet_pokemon.vet_backend.model.Comuna;
import vet_pokemon.vet_backend.service.ComunaService;

@Tag(name = "Comunas", description = "Operaciones CRUD para comunas")
@RestController
@RequestMapping("/api/v1/comunas")
public class ComunaController {

    @Autowired
    private ComunaService comunaService;

    @Autowired
    private ComunaModelAssembler assembler;

    @Operation(summary = "Listar todas las comunas", description = "Retorna la lista completa de comunas registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ComunaDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen comunas registradas", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ComunaDTO>>> obtenerTodos() {
        List<EntityModel<ComunaDTO>> lista = comunaService.listarComunas().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ComunaController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener comuna por ID", description = "Retorna los datos de la comuna indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comuna encontrada", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ComunaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Comuna no encontrada con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ComunaDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            ComunaDTO dto = comunaService.obtenerComunaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar comunas de una región", description = "Retorna las comunas que pertenecen a la región indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comunas de la región encontradas", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ComunaDTO.class))),
        @ApiResponse(responseCode = "204", description = "La región no tiene comunas", content = @Content)
    })
    @GetMapping(value = "/region/{idRegion}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ComunaDTO>>> listarPorRegion(@PathVariable Integer idRegion) {
        List<EntityModel<ComunaDTO>> lista = comunaService.listarPorRegion(idRegion).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(ComunaController.class).listarPorRegion(idRegion)).withSelfRel()));
    }

    @Operation(summary = "Crear comuna", description = "Registra una nueva comuna en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comuna creada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ComunaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o comuna ya registrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ComunaDTO>> crear(@Valid @RequestBody Comuna nueva) {
        try {
            ComunaDTO guardado = comunaService.guardarComuna(nueva);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ComunaDTO dto = comunaService.obtenerComunaPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(ComunaController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar comuna", description = "Modifica los datos de la comuna indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comuna actualizada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = ComunaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comuna no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ComunaDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Comuna datos) {
        try {
            comunaService.actualizarComuna(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            ComunaDTO dto = comunaService.obtenerComunaPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar comuna", description = "Elimina de la base de datos la comuna indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comuna eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comuna no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usada por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            comunaService.eliminarComuna(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
