package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.MetodoPago;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    @Query("SELECT m FROM MetodoPago m WHERE m.nombremetodopago = :nombremetodopago")
    List<MetodoPago> findByNombre(@Param("nombremetodopago") String nombremetodopago);
}