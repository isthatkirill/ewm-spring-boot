package isthatkirill.main.event.dto;

import isthatkirill.main.location.dto.LocationDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewEventDtoTest {

    @Autowired
    private JacksonTester<NewEventDto> json;

    private final NewEventDto newEventDto = NewEventDto.builder()
            .annotation("annotation_at_least_20_char")
            .description("description_at_least_20_char")
            .category(1L)
            .eventDate(LocalDateTime.now().plusHours(3))
            .location(LocationDto.builder().lat(10.10f).lon(20.20f).build())
            .paid(false)
            .participantLimit(100)
            .requestModeration(false)
            .title("title_at_least_3_char")
            .build();

    @Test
    @SneakyThrows
    void eventShortDtoTest() {
        JsonContent<NewEventDto> result = json.write(newEventDto);

        assertThat(result)
                .hasJsonPathStringValue("$.annotation", newEventDto.getAnnotation())
                .hasJsonPathStringValue("$.eventDate", newEventDto.getEventDate())
                .hasJsonPathBooleanValue("$.paid", newEventDto.getPaid())
                .hasJsonPathStringValue("$.title", newEventDto.getTitle())
                .hasJsonPathStringValue("$.description", newEventDto.getDescription())
                .hasJsonPathNumberValue("$.category", newEventDto.getCategory())
                .hasJsonPathNumberValue("$.location.lat", newEventDto.getLocation().getLat())
                .hasJsonPathNumberValue("$.location.lon", newEventDto.getLocation().getLon())
                .hasJsonPathNumberValue("$.participantLimit", newEventDto.getParticipantLimit())
                .hasJsonPathBooleanValue("$.requestModeration", newEventDto.getRequestModeration());
    }

    @Test
    @SneakyThrows
    void eventShortDtoWithNullFieldsTest() {
        newEventDto.setEventDate(null);
        newEventDto.setCategory(null);
        JsonContent<NewEventDto> result = json.write(newEventDto);

        assertThat(result)
                .hasJsonPathStringValue("$.annotation", newEventDto.getAnnotation())
                .hasJsonPathBooleanValue("$.paid", newEventDto.getPaid())
                .hasJsonPathStringValue("$.title", newEventDto.getTitle())
                .hasJsonPathStringValue("$.description", newEventDto.getDescription())
                .hasJsonPathNumberValue("$.location.lat", newEventDto.getLocation().getLat())
                .hasJsonPathNumberValue("$.location.lon", newEventDto.getLocation().getLon())
                .hasJsonPathNumberValue("$.participantLimit", newEventDto.getParticipantLimit())
                .hasJsonPathBooleanValue("$.requestModeration", newEventDto.getRequestModeration())
                .hasEmptyJsonPathValue("$.category")
                .hasEmptyJsonPathValue("$.eventDate");
    }

}