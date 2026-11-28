package vet_pokemon.vet_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    boolean existsByNombreRegionIgnoreCase(String nombreRegion);
}