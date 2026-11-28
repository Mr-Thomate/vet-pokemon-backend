package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.ComunaController;
import vet_pokemon.vet_backend.dto.ComunaDTO;

@Component
public class ComunaModelAssembler implements RepresentationModelAssembler<ComunaDTO, EntityModel<ComunaDTO>> {
    @Override
    public EntityModel<ComunaDTO> toModel(ComunaDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ComunaController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(ComunaController.class).obtenerTodos()).withRel("todos-los-comunas")
        );
    }
}
