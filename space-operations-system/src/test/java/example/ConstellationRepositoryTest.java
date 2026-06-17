package org.example;

import org.example.domains.CommunicationSatellite;
import org.example.domains.SatelliteConstellation;
import org.example.repository.SatelliteConstellationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConstellationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SatelliteConstellationRepository constellationRepository;

    @Test
    void shouldSaveAndFindConstellation() {
        SatelliteConstellation constellation = new SatelliteConstellation("TestConstellation");
        SatelliteConstellation saved = constellationRepository.save(constellation);
        entityManager.flush();

        Optional<SatelliteConstellation> found = constellationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getConstellationName()).isEqualTo("TestConstellation");
    }

    @Test
    void shouldFindByConstellationName() {
        SatelliteConstellation constellation = new SatelliteConstellation("StarLink");
        constellationRepository.save(constellation);
        entityManager.flush();

        Optional<SatelliteConstellation> found = constellationRepository.findByConstellationName("StarLink");
        assertThat(found).isPresent();
    }

    @Test
    void shouldAddSatelliteToConstellation() {
        SatelliteConstellation constellation = new SatelliteConstellation("GalaxyNet");
        CommunicationSatellite satellite = new CommunicationSatellite("CommSat-1", 0.9, 100.0);
        constellation.addSatellite(satellite);

        SatelliteConstellation saved = constellationRepository.save(constellation);
        entityManager.flush();

        SatelliteConstellation found = constellationRepository.findByIdWithSatellites(saved.getId()).orElseThrow();
        assertThat(found.getSatellites()).hasSize(1);
        assertThat(found.getSatellites().get(0).getName()).isEqualTo("CommSat-1");
    }
}