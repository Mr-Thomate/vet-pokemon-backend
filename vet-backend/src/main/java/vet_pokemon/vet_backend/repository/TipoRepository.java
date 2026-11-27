package vet_pokemon.vet_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Tipo;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
}