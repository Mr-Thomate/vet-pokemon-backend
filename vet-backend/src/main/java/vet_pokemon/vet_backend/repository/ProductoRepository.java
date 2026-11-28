package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Producto;

@Repository 
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p JOIN p.categoria c WHERE c.id = :idCategoria")
    List<Producto> findByIdCategoria(@Param("idCategoria") Integer idCategoria);

    @Query("SELECT p FROM Producto p JOIN p.tipo t WHERE t.id = :idTipo")
    List<Producto> findByIdTipo(@Param("idTipo") Integer idTipo);
}