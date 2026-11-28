package vet_pokemon.vet_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vet_pokemon.vet_backend.model.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {


    // Detalles de una venta
    @Query("SELECT d FROM DetalleVenta d JOIN d.venta v WHERE v.id = :idVenta")
    List<DetalleVenta> findByIdVenta(@Param("idVenta") Integer idVenta);

    @Query("SELECT d FROM DetalleVenta d JOIN d.producto p WHERE p.id = :idProducto")
    List<DetalleVenta> findByIdProducto(@Param("idProducto") Integer idProducto);
}
