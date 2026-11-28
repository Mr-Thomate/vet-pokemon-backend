package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.CategoriaController;
import vet_pokemon.vet_backend.dto.CategoriaDTO;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<CategoriaDTO, EntityModel<CategoriaDTO>> {
    @Override
    public EntityModel<CategoriaDTO> toModel(CategoriaDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CategoriaController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).obtenerTodos()).withRel("todos-los-categorias")
        );
    }
}
