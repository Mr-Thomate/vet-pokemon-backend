package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.VentaController;
import vet_pokemon.vet_backend.dto.VentaDTO;

@Component
public class VentaModelAssembler implements RepresentationModelAssembler<VentaDTO, EntityModel<VentaDTO>> {
    @Override
    public EntityModel<VentaDTO> toModel(VentaDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(VentaController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).obtenerTodos()).withRel("todos-los-ventas")
        );
    }
}
