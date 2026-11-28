package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.MetodoEnvio;

@Repository
public interface MetodoEnvioRepository extends JpaRepository<MetodoEnvio, Integer> {

    // MetodoEnvio no tiene un campo "nombre" (antes buscaba r.nombre); se busca por tipoEnvio
    @Query("SELECT m FROM MetodoEnvio m WHERE m.tipoEnvio = :tipoEnvio")
    List<MetodoEnvio> findByTipoEnvio(@Param("tipoEnvio") String tipoEnvio);
}
