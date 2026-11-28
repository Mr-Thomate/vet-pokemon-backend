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
import vet_pokemon.vet_backend.assembler.BlogModelAssembler;
import vet_pokemon.vet_backend.dto.BlogDTO;
import vet_pokemon.vet_backend.model.Blog;
import vet_pokemon.vet_backend.service.BlogService;

@Tag(name = "Blogs", description = "Operaciones CRUD para blogs")
@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogModelAssembler assembler;

    @Operation(summary = "Listar todos los blogs", description = "Retorna la lista completa de blogs registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "204", description = "No existen blogs registrados", content = @Content)
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<BlogDTO>>> obtenerTodos() {
        List<EntityModel<BlogDTO>> lista = blogService.listarBlogs().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(BlogController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "Obtener blog por ID", description = "Retorna los datos del blog indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog encontrado", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "404", description = "Blog no encontrado con el ID proporcionado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BlogDTO>> obtenerPorId(@PathVariable Integer id) {
        try {
            BlogDTO dto = blogService.obtenerBlogPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar blogs por título", description = "Retorna los blogs cuyo título contiene el texto indicado (sin distinguir mayúsculas)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blogs encontrados", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "204", description = "No se encontraron blogs con ese título", content = @Content)
    })
    @GetMapping(value = "/titulo/{titulo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<BlogDTO>>> buscarPorTitulo(@PathVariable String titulo) {
        List<EntityModel<BlogDTO>> lista = blogService.buscarPorTitulo(titulo).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(BlogController.class).buscarPorTitulo(titulo)).withSelfRel()));
    }

    @Operation(summary = "Crear blog", description = "Registra un nuevo blog en la base de datos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blog creado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o blog ya registrado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BlogDTO>> crear(@Valid @RequestBody Blog nuevo) {
        try {
            BlogDTO guardado = blogService.guardarBlog(nuevo);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            BlogDTO dto = blogService.obtenerBlogPorId(guardado.getId());
            return ResponseEntity
                    .created(linkTo(methodOn(BlogController.class).obtenerPorId(dto.getId())).toUri())
                    .body(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar blog", description = "Modifica los datos del blog indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog actualizado con éxito", content = @Content(mediaType = "application/hal+json", schema = @Schema(implementation = BlogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Blog no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflicto con datos ya existentes", content = @Content)
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<BlogDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody Blog datos) {
        try {
            blogService.actualizarBlog(id, datos);
            // Se vuelve a buscar para que el DTO traiga los nombres de las relaciones ya cargados
            BlogDTO dto = blogService.obtenerBlogPorId(id);
            return ResponseEntity.ok(assembler.toModel(dto));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar blog", description = "Elimina de la base de datos el blog indicado según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Blog eliminado con éxito", content = @Content),
        @ApiResponse(responseCode = "404", description = "Blog no encontrado con el ID proporcionado", content = @Content),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar porque está siendo usado por otros registros", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            blogService.eliminarBlog(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
