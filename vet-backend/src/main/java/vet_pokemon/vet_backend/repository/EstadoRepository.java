package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

    @Query("SELECT e FROM Estado e WHERE e.descripcion = :descripcion")
    List<Estado> findByDescripcion(@Param("descripcion") String descripcion);
    
}
