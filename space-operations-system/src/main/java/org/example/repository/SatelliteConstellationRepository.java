package org.example.repository;

import org.example.domains.SatelliteConstellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SatelliteConstellationRepository extends JpaRepository<SatelliteConstellation, Long> {

    Optional<SatelliteConstellation> findByConstellationName(String constellationName);

    boolean existsByConstellationName(String constellationName);

    void deleteByConstellationName(String constellationName);

    @Query("SELECT sc FROM SatelliteConstellation sc LEFT JOIN FETCH sc.satellites WHERE sc.id = :id")
    Optional<SatelliteConstellation> findByIdWithSatellites(@Param("id") Long id);

    @Query("SELECT sc FROM SatelliteConstellation sc LEFT JOIN FETCH sc.satellites")
    List<SatelliteConstellation> findAllWithSatellites();
}