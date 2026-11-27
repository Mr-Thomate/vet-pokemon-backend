package vet_pokemon.vet_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import vet_pokemon.vet_backend.model.MetodoEnvio;


@Repository 
public interface MetodoEnvioRepository extends JpaRepository<MetodoEnvio, Integer> {

    @Query ("SELECT r FROM MetodoEnvio r WHERE r.nombre = :nombre")
    List<MetodoEnvio> findByNombre(@Param ("nombre") String nombre);
  
}
