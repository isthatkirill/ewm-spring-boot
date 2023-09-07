package isthatkirill.main.location.repository;

import isthatkirill.main.location.model.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-locations.sql"})
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void findByLatAndLonTest() {
        Float lat = 10.10f;
        Float lon = 20.20f;

        Optional<Location> optionalLocation = locationRepository.findByLatAndLon(lat, lon);

        assertThat(optionalLocation).isPresent()
                .get()
                .hasFieldOrPropertyWithValue("lat", lat)
                .hasFieldOrPropertyWithValue("lon", lon);
    }
}