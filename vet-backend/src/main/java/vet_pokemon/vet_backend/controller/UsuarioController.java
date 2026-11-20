package vet_pokemon.vet_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import vet_pokemon.vet_backend.assembler.UsuarioModelAssembler;
import vet_pokemon.vet_backend.dto.UsuarioDTO;
import vet_pokemon.vet_backend.service.UsuarioService;

@Tag(name = "Usuarios", description = "Operaciones CRUD para los usuarios")
@RestController 
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired 
    private UsuarioService usuarioService;

    @Autowired 
    private UsuarioModelAssembler assembler;
    
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = UsuarioDTO.class))),
        @ApiResponse(responseCode = "204", description = "Sin contenido", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<UsuarioDTO>>> obtenerTodos() {
        List<EntityModel<UsuarioDTO>> usuarios = usuarioService.obtenerTodos().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (usuarios.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(usuarios, linkTo(methodOn(UsuarioController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = UsuarioDTO.class))),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            UsuarioDTO dto = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
