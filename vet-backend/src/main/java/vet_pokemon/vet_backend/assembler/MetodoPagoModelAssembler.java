package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.MetodoPagoController;
import vet_pokemon.vet_backend.dto.MetodoPagoDTO;

@Component
public class MetodoPagoModelAssembler implements RepresentationModelAssembler<MetodoPagoDTO, EntityModel<MetodoPagoDTO>> {
    @Override
    public EntityModel<MetodoPagoDTO> toModel(MetodoPagoDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MetodoPagoController.class).obtenerPorId(dto.getId_MetodoPago())).withSelfRel(),
                linkTo(methodOn(MetodoPagoController.class).obtenerTodos()).withRel("todos-los-metodos-pago")
        );
    }
}
