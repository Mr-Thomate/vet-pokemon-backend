package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    // Buscar por rol
    @Query ("SELECT u FROM Usuario u JOIN u.rol r WHERE r.id = :idRol")
    List<Usuario> findByIdRol(@Param ("idRol") Integer idRol);

    boolean existsByCorreoIgnoreCase(String correo);
}
