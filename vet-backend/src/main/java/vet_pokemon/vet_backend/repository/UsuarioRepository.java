import vet_pokemon.vet_backend.repository;

import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    // Buscar por rol
    @Query("SELECT u FROM Usuario u JOIN u.rol r WHERE r.id = :idRol")
    List<Empleado> findByIdRol(@Param("idRol") Integer idRol);
}