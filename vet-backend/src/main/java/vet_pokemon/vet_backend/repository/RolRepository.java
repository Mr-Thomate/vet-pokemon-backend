package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    @Query("SELECT r FROM Rol r WHERE r.nombre = :nombre")
    List<Rol> findByNombre(@Param("nombre") String nombre);
}