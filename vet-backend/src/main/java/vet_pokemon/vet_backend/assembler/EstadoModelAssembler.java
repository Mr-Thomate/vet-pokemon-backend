package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.EstadoController;
import vet_pokemon.vet_backend.dto.EstadoDTO;

@Component
public class EstadoModelAssembler implements RepresentationModelAssembler<EstadoDTO, EntityModel<EstadoDTO>> {
    @Override
    public EntityModel<EstadoDTO> toModel(EstadoDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EstadoController.class).obtenerPorId(dto.getId_estado())).withSelfRel(),
                linkTo(methodOn(EstadoController.class).obtenerTodos()).withRel("todos-los-estados")
        );
    }
}
