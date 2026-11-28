package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.TipoController;
import vet_pokemon.vet_backend.dto.TipoDTO;

@Component
public class TipoModelAssembler implements RepresentationModelAssembler<TipoDTO, EntityModel<TipoDTO>> {
    @Override
    public EntityModel<TipoDTO> toModel(TipoDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(TipoController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(TipoController.class).obtenerTodos()).withRel("todos-los-tipos")
        );
    }
}
