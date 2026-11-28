package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.ImagenProducto;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Integer> {

    @Query("SELECT i FROM ImagenProducto i JOIN i.producto p WHERE p.id = :idProducto")
    List<ImagenProducto> findByIdProducto(@Param("idProducto") Integer idProducto);
}
