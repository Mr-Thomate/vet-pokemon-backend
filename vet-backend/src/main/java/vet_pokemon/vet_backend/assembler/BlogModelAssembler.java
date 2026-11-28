package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.BlogController;
import vet_pokemon.vet_backend.dto.BlogDTO;

@Component
public class BlogModelAssembler implements RepresentationModelAssembler<BlogDTO, EntityModel<BlogDTO>> {
    @Override
    public EntityModel<BlogDTO> toModel(BlogDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(BlogController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(BlogController.class).obtenerTodos()).withRel("todos-los-blogs")
        );
    }
}
