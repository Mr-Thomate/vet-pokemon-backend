package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.MetodoEnvioController;
import vet_pokemon.vet_backend.dto.MetodoEnvioDTO;

@Component
public class MetodoEnvioModelAssembler implements RepresentationModelAssembler<MetodoEnvioDTO, EntityModel<MetodoEnvioDTO>> {
    @Override
    public EntityModel<MetodoEnvioDTO> toModel(MetodoEnvioDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MetodoEnvioController.class).obtenerPorId(dto.getId_Envio())).withSelfRel(),
                linkTo(methodOn(MetodoEnvioController.class).obtenerTodos()).withRel("todos-los-metodos-envio")
        );
    }
}
