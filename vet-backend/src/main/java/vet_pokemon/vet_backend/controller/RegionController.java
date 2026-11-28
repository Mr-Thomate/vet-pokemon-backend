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
import vet_pokemon.vet_backend.assembler.RegionModelAssembler;
import vet_pokemon.vet_backend.dto.RegionDTO;
import vet_pokemon.vet_backend.model.Region;
import vet_pokemon.vet_backend.service.RegionService;

@Tag(name = "Regiones", description = "Operaciones CRUD para regiones")
@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionModelAssembler assembler;

    @Operation(summary = "Listar todas las regiones", description = "Retorna la lista completa de regiones registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RegionDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen regiones registradas", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<RegionDTO>>> obtenerTodos() {
        List<EntityModel<RegionDTO>> lista = regionService.listarRegiones().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(RegionController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener región por ID", description = "Retorna los datos de la región indicada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Región encontrada", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RegionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Región no encontrada con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RegionDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            RegionDTO dto = regionService.obtenerRegionPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear región", description = "Registra una nueva región en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Región creada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RegionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o región ya registrada", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RegionDTO>> crear(@Valid @RequestBody Region nueva) {
        try {
            RegionDTO guardado = regionService.guardarRegion(nueva);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            RegionDTO dto = regionService.obtenerRegionPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(RegionController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar región", description = "Modifica los datos de la región indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Región actualizada con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = RegionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Región no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<RegionDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Region datos) {
        try {
            regionService.actualizarRegion(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            RegionDTO dto = regionService.obtenerRegionPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar región", description = "Elimina de la base de datos la región indicada según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Región eliminada con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Región no encontrada con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usada por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            regionService.eliminarRegion(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
