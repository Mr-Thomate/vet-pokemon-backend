package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // En la entidad el campo se llama nombreRol (antes buscaba r.nombre, que no existe)
    @Query("SELECT r FROM Rol r WHERE r.nombreRol = :nombre")
    List<Rol> findByNombre(@Param("nombre") String nombre);
    boolean existsByNombreRolIgnoreCase(String nombreRol);

}
