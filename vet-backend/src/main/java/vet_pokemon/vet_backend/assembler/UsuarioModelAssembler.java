package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.UsuarioController;
import vet_pokemon.vet_backend.dto.UsuarioDTO;

@Component 
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioDTO, EntityModel<UsuarioDTO>> {
    @Override 
    public EntityModel<UsuarioDTO> toModel(UsuarioDTO dto){
        return EntityModel.of(dto,
                linkTo(methodOn(UsuarioController.class).obtenerPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerTodos()).withRel("todos-los-usuarios")
        );
    }
}
