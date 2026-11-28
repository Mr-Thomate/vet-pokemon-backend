package vet_pokemon.vet_backend.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import vet_pokemon.vet_backend.controller.DetalleVentaController;
import vet_pokemon.vet_backend.dto.DetalleVentaDTO;

@Component
public class DetalleVentaModelAssembler implements RepresentationModelAssembler<DetalleVentaDTO, EntityModel<DetalleVentaDTO>> {
    @Override
    public EntityModel<DetalleVentaDTO> toModel(DetalleVentaDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(DetalleVentaController.class).obtenerPorId(dto.getId_DetalleVenta())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).obtenerTodos()).withRel("todos-los-detalles-venta")
        );
    }
}
