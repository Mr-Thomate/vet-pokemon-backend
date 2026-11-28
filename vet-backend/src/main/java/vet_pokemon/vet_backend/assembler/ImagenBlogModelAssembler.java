package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.ImagenBlogController;
import vet_pokemon.vet_backend.dto.ImagenBlogDTO;

@Component
public class ImagenBlogModelAssembler implements RepresentationModelAssembler<ImagenBlogDTO, EntityModel<ImagenBlogDTO>> {
    @Override
    public EntityModel<ImagenBlogDTO> toModel(ImagenBlogDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ImagenBlogController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(ImagenBlogController.class).obtenerTodos()).withRel("todos-los-imagenes-blog")
        );
    }
}
