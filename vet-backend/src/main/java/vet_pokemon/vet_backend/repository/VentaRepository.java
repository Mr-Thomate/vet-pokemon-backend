package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @Query("SELECT v FROM Venta v JOIN v.usuario u WHERE u.id = :idUsuario")
    List<Venta> findByIdUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT v FROM Venta v JOIN v.estado e WHERE e.id_estado = :idEstado")
    List<Venta> findByIdEstado(@Param("idEstado") Integer idEstado);
}