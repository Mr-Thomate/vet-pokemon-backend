package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.ImagenBlog;

@Repository
public interface ImagenBlogRepository extends JpaRepository<ImagenBlog, Integer> {

    @Query("SELECT i FROM ImagenBlog i JOIN i.blog b WHERE b.id = :idBlog")
    List<ImagenBlog> findByIdBlog(@Param("idBlog") Integer idBlog);
}