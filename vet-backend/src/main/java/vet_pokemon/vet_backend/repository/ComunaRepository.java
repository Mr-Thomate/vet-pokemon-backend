package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Comuna;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {

    // Comunas que pertenecen a una región
    @Query("SELECT c FROM Comuna c JOIN c.region r WHERE r.id = :idRegion")
    List<Comuna> findByIdRegion(@Param("idRegion") Integer idRegion);
}
