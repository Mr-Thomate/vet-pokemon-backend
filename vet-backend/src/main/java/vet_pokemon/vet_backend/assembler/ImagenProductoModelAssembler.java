package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.ImagenProductoController;
import vet_pokemon.vet_backend.dto.ImagenProductoDTO;

@Component
public class ImagenProductoModelAssembler implements RepresentationModelAssembler<ImagenProductoDTO, EntityModel<ImagenProductoDTO>> {
    @Override
    public EntityModel<ImagenProductoDTO> toModel(ImagenProductoDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ImagenProductoController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(ImagenProductoController.class).obtenerTodos()).withRel("todos-los-imagenes-producto")
        );
    }
}
