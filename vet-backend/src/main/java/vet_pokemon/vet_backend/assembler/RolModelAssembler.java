package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.RolController;
import vet_pokemon.vet_backend.dto.RolDTO;

@Component
public class RolModelAssembler implements RepresentationModelAssembler<RolDTO, EntityModel<RolDTO>> {
    @Override
    public EntityModel<RolDTO> toModel(RolDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(RolController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(RolController.class).obtenerTodos()).withRel("todos-los-roles")
        );
    }
}
