package isthatkirill.main.location.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class LocationDtoTest {

    @Autowired
    private JacksonTester<LocationDto> json;

    private final LocationDto locationDto = LocationDto.builder()
            .lat(15.43f)
            .lon(9.52f)
            .build();


    @Test
    @SneakyThrows
    void locationDtoTest() {
        JsonContent<LocationDto> result = json.write(locationDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.lat", locationDto.getLat())
                .hasJsonPathNumberValue("$.lon", locationDto.getLon());
    }

    @Test
    @SneakyThrows
    void locationDtoWithNullFieldsTest() {
        locationDto.setLat(null);

        JsonContent<LocationDto> result = json.write(locationDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.lon", locationDto.getLon())
                .hasEmptyJsonPathValue("$.lat");
    }

}