package vet_pokemon.vet_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vet_pokemon.vet_backend.dto.RegionDTO;
import vet_pokemon.vet_backend.model.Region;
import vet_pokemon.vet_backend.repository.RegionRepository;

@Service
@Transactional
public class RegionService {

    @Autowired
    private RegionRepository regionRepository;

    private RegionDTO convertirADTO(Region region) {
        RegionDTO dto = new RegionDTO();

        dto.setId(region.getId());
        dto.setNombreregion(region.getNombreRegion());

        return dto;
    }

    public List<RegionDTO> listarRegiones() {
        List<Region> regiones = regionRepository.findAll();
        List<RegionDTO> listaDTO = new ArrayList<>();

        for (Region region : regiones) {
            listaDTO.add(convertirADTO(region));
        }
        return listaDTO;
    }

    public RegionDTO obtenerRegionPorId(Integer id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region no encontrada con ID: " + id));
        return convertirADTO(region);
    }

    public RegionDTO guardarRegion(Region region) {
        if (regionRepository.existsByNombreRegionIgnoreCase(region.getNombreRegion())) {
            throw new RuntimeException("La region ya se encuentra registrada");
        }
        Region regionGuardada = regionRepository.save(region);
        return convertirADTO(regionGuardada);
    }

    public RegionDTO actualizarRegion(Integer id, Region datosActualizados) {
        Region regionExistente = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region no encontrada con ID: " + id));

        regionExistente.setNombreRegion(datosActualizados.getNombreRegion());

        Region regionActualizada = regionRepository.save(regionExistente);
        return convertirADTO(regionActualizada);
    }

    public void eliminarRegion(Integer id) {
        if (!regionRepository.existsById(id)) {
            throw new RuntimeException("Region no encontrada con ID: " + id);
        }
        regionRepository.deleteById(id);
    }
}