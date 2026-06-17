package org.example.repository;

import org.example.domains.Satellite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SatelliteRepository extends JpaRepository<Satellite, Long> {

    List<Satellite> findByConstellationId(Long constellationId);

    List<Satellite> findByConstellationConstellationName(String constellationName);

    Optional<Satellite> findByNameAndConstellationConstellationName(String name, String constellationName);

    @Query("SELECT s FROM Satellite s WHERE s.constellation.constellationName = :constellationName AND s.isActive = true")
    List<Satellite> findActiveSatellitesByConstellationName(@Param("constellationName") String constellationName);

    @Query("SELECT COUNT(s) FROM Satellite s WHERE s.constellation.constellationName = :constellationName")
    long countByConstellationName(@Param("constellationName") String constellationName);

    void deleteByNameAndConstellationConstellationName(String name, String constellationName);
}