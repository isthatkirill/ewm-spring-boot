package isthatkirill.main.event.dto;

import isthatkirill.main.event.model.EventStateAction;
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
class UpdateEventDtoTest {

    @Autowired
    private JacksonTester<UpdateEventDto> json;

    private final UpdateEventDto updateEventDto = UpdateEventDto.builder()
            .description("description_description_at_least_20_char")
            .annotation("annotation_annotation_at_least_20_char")
            .eventDate(LocalDateTime.now().plusHours(4))
            .category(1L)
            .location(LocationDto.builder().lon(10.10f).lat(20.20f).build())
            .paid(false)
            .participantLimit(100)
            .requestModeration(false)
            .title("title_at_least_3_char")
            .stateAction(EventStateAction.PUBLISH_EVENT)
            .build();

    @Test
    @SneakyThrows
    void updateEventDtoTest() {
        JsonContent<UpdateEventDto> result = json.write(updateEventDto);

        assertThat(result)
                .hasJsonPathStringValue("$.annotation", updateEventDto.getAnnotation())
                .hasJsonPathStringValue("$.eventDate", updateEventDto.getEventDate())
                .hasJsonPathBooleanValue("$.paid", updateEventDto.getPaid())
                .hasJsonPathStringValue("$.title", updateEventDto.getTitle())
                .hasJsonPathStringValue("$.stateAction", updateEventDto.getStateAction().name())
                .hasJsonPathStringValue("$.description", updateEventDto.getDescription())
                .hasJsonPathNumberValue("$.category", updateEventDto.getCategory())
                .hasJsonPathNumberValue("$.location.lat", updateEventDto.getLocation().getLat())
                .hasJsonPathNumberValue("$.location.lon", updateEventDto.getLocation().getLon())
                .hasJsonPathNumberValue("$.participantLimit", updateEventDto.getParticipantLimit())
                .hasJsonPathBooleanValue("$.requestModeration", updateEventDto.getRequestModeration());
    }

    @Test
    @SneakyThrows
    void updateEventDtoWithNullFieldsTest() {
        updateEventDto.setEventDate(null);
        updateEventDto.setLocation(null);
        JsonContent<UpdateEventDto> result = json.write(updateEventDto);

        assertThat(result)
                .hasJsonPathStringValue("$.annotation", updateEventDto.getAnnotation())
                .hasJsonPathBooleanValue("$.paid", updateEventDto.getPaid())
                .hasJsonPathStringValue("$.title", updateEventDto.getTitle())
                .hasJsonPathStringValue("$.stateAction", updateEventDto.getStateAction().name())
                .hasJsonPathStringValue("$.description", updateEventDto.getDescription())
                .hasJsonPathNumberValue("$.category", updateEventDto.getCategory())
                .hasJsonPathNumberValue("$.participantLimit", updateEventDto.getParticipantLimit())
                .hasJsonPathBooleanValue("$.requestModeration", updateEventDto.getRequestModeration())
                .hasEmptyJsonPathValue("$.eventDate")
                .hasEmptyJsonPathValue("$.location");
    }

}