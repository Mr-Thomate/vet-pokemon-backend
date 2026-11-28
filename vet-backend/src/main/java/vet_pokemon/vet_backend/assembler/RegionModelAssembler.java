package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.RegionController;
import vet_pokemon.vet_backend.dto.RegionDTO;

@Component
public class RegionModelAssembler implements RepresentationModelAssembler<RegionDTO, EntityModel<RegionDTO>> {
    @Override
    public EntityModel<RegionDTO> toModel(RegionDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(RegionController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(RegionController.class).obtenerTodos()).withRel("todos-los-regiones")
        );
    }
}
