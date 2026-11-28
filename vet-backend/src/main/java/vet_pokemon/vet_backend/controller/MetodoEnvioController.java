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
import vet_pokemon.vet_backend.assembler.MetodoEnvioModelAssembler;
import vet_pokemon.vet_backend.dto.MetodoEnvioDTO;
import vet_pokemon.vet_backend.model.MetodoEnvio;
import vet_pokemon.vet_backend.service.MetodoEnvioService;

@Tag(name = "Métodos de envío", description = "Operaciones CRUD para métodos de envío")
@RestController
@RequestMapping("/api/v1/metodos-envio")
public class MetodoEnvioController {

    @Autowired
    private MetodoEnvioService metodoEnvioService;

    @Autowired
    private MetodoEnvioModelAssembler assembler;

    @Operation(summary = "Listar todos los métodos de envío", description = "Retorna la lista completa de métodos de envío registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoEnvioDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen métodos de envío registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<MetodoEnvioDTO>>> obtenerTodos() {
        List<EntityModel<MetodoEnvioDTO>> lista = metodoEnvioService.listarMetodosEnvio().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(MetodoEnvioController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener método de envío por ID", description = "Retorna los datos del método de envío indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Método de envío encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoEnvioDTO.class))),
        @ApiResponse(responseCode = "404", description = "Método de envío no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<MetodoEnvioDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            MetodoEnvioDTO dto = metodoEnvioService.obtenerMetodoEnvioPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar métodos de envío por tipo", description = "Retorna los métodos de envío cuyo tipo coincide con el valor indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métodos de envío encontrados", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoEnvioDTO.class))),
        @ApiResponse(responseCode = "204", description = "No se encontraron métodos de envío de ese tipo", content = @Content)
    })
    @GetMapping(value = "/tipo/{tipoEnvio}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<MetodoEnvioDTO>>> buscarPorTipoEnvio(@PathVariable String tipoEnvio) {
        List<EntityModel<MetodoEnvioDTO>> lista = metodoEnvioService.buscarPorTipoEnvio(tipoEnvio).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(MetodoEnvioController.class).buscarPorTipoEnvio(tipoEnvio)).withSelfRel()));
    }

    @Operation(summary = "Crear método de envío", description = "Registra un nuevo método de envío en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Método de envío creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoEnvioDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o método de envío ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<MetodoEnvioDTO>> crear(@Valid @RequestBody MetodoEnvio nuevo) {
        try {
            MetodoEnvioDTO guardado = metodoEnvioService.guardarMetodoEnvio(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            MetodoEnvioDTO dto = metodoEnvioService.obtenerMetodoEnvioPorId(guardado.getId_Envio());
            return ResponseEntity
                    .created(linkTo(methodOn(MetodoEnvioController.class).obtenerPorId(dto.getId_Envio())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar método de envío", description = "Modifica los datos del método de envío indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Método de envío actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoEnvioDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Método de envío no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<MetodoEnvioDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody MetodoEnvio datos) {
        try {
            metodoEnvioService.actualizarMetodoEnvio(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            MetodoEnvioDTO dto = metodoEnvioService.obtenerMetodoEnvioPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar método de envío", description = "Elimina de la base de datos el método de envío indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Método de envío eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Método de envío no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            metodoEnvioService.eliminarMetodoEnvio(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
