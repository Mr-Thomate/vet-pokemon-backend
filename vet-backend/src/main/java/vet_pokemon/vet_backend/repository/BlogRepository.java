package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    boolean existsByTituloIgnoreCase(String titulo);

    List<Blog> findByTituloContainingIgnoreCase(String titulo);
}
