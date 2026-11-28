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
import vet_pokemon.vet_backend.assembler.MetodoPagoModelAssembler;
import vet_pokemon.vet_backend.dto.MetodoPagoDTO;
import vet_pokemon.vet_backend.model.MetodoPago;
import vet_pokemon.vet_backend.service.MetodoPagoService;

@Tag(name = "Métodos de pago", description = "Operaciones CRUD para métodos de pago")
@RestController
@RequestMapping("/api/v1/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    @Autowired
    private MetodoPagoModelAssembler assembler;

    @Operation(summary = "Listar todos los métodos de pago", description = "Retorna la lista completa de métodos de pago registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoPagoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen métodos de pago registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<MetodoPagoDTO>>> obtenerTodos() {
        List<EntityModel<MetodoPagoDTO>> lista = metodoPagoService.listarMetodosPago().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(MetodoPagoController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener método de pago por ID", description = "Retorna los datos del método de pago indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Método de pago encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoPagoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Método de pago no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<MetodoPagoDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            MetodoPagoDTO dto = metodoPagoService.obtenerMetodoPagoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar métodos de pago por nombre", description = "Retorna los métodos de pago cuyo nombre coincide con el valor indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métodos de pago encontrados", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoPagoDTO.class))),
        @ApiResponse(responseCode = "204", description = "No se encontraron métodos de pago con ese nombre", content = @Content)
    })
    @GetMapping(value = "/nombre/{nombre}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<MetodoPagoDTO>>> buscarPorNombre(@PathVariable String nombre) {
        List<EntityModel<MetodoPagoDTO>> lista = metodoPagoService.buscarPorNombre(nombre).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(MetodoPagoController.class).buscarPorNombre(nombre)).withSelfRel()));
    }

    @Operation(summary = "Crear método de pago", description = "Registra un nuevo método de pago en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Método de pago creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoPagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o método de pago ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<MetodoPagoDTO>> crear(@Valid @RequestBody MetodoPago nuevo) {
        try {
            MetodoPagoDTO guardado = metodoPagoService.guardarMetodoPago(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            MetodoPagoDTO dto = metodoPagoService.obtenerMetodoPagoPorId(guardado.getId_MetodoPago());
            return ResponseEntity
                    .created(linkTo(methodOn(MetodoPagoController.class).obtenerPorId(dto.getId_MetodoPago())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar método de pago", description = "Modifica los datos del método de pago indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Método de pago actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = MetodoPagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Método de pago no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<MetodoPagoDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody MetodoPago datos) {
        try {
            metodoPagoService.actualizarMetodoPago(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            MetodoPagoDTO dto = metodoPagoService.obtenerMetodoPagoPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar método de pago", description = "Elimina de la base de datos el método de pago indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Método de pago eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Método de pago no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            metodoPagoService.eliminarMetodoPago(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
