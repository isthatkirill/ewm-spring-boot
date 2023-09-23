package isthatkirill.main.event.dto;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.user.dto.UserShortDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class EventFullDtoTest {

    @Autowired
    private JacksonTester<EventFullDto> json;

    private final EventFullDto eventFullDto = EventFullDto.builder()
            .id(1L)
            .description("description_description_at_least_20_char")
            .annotation("annotation_annotation_at_least_20_char")
            .eventDate(LocalDateTime.now().plusHours(4))
            .category(CategoryDto.builder().id(1L).name("cat_name").build())
            .location(LocationDto.builder().lon(10.10f).lat(20.20f).build())
            .paid(false)
            .createdOn(LocalDateTime.now())
            .confirmedRequests(200L)
            .views(200L)
            .initiator(UserShortDto.builder().id(1L).name("user_name").build())
            .participantLimit(100)
            .requestModeration(false)
            .title("title_at_least_3_char")
            .state(EventState.PUBLISHED)
            .build();

    @Test
    @SneakyThrows
    void eventFullDtoTest() {
        JsonContent<EventFullDto> result = json.write(eventFullDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", eventFullDto.getId())
                .hasJsonPathStringValue("$.description", eventFullDto.getDescription())
                .hasJsonPathStringValue("$.annotation", eventFullDto.getAnnotation())
                .hasJsonPathNumberValue("$.category.id", eventFullDto.getCategory().getId())
                .hasJsonPathStringValue("$.category.name", eventFullDto.getCategory().getName())
                .hasJsonPathNumberValue("$.confirmedRequests", eventFullDto.getConfirmedRequests())
                .hasJsonPathStringValue("$.eventDate", eventFullDto.getEventDate())
                .hasJsonPathStringValue("$.createdOn", eventFullDto.getCreatedOn())
                .hasJsonPathNumberValue("$.location.lat", eventFullDto.getLocation().getLat())
                .hasJsonPathNumberValue("$.location.lon", eventFullDto.getLocation().getLon())
                .hasJsonPathNumberValue("$.initiator.id", eventFullDto.getInitiator().getId())
                .hasJsonPathStringValue("$.initiator.name", eventFullDto.getInitiator().getName())
                .hasJsonPathNumberValue("$.participantLimit", eventFullDto.getParticipantLimit())
                .hasJsonPathBooleanValue("$.requestModeration", eventFullDto.getRequestModeration())
                .hasJsonPathStringValue("$.state", eventFullDto.getState().name())
                .hasJsonPathBooleanValue("$.paid", eventFullDto.getPaid())
                .hasJsonPathStringValue("$.title", eventFullDto.getTitle())
                .hasJsonPathNumberValue("$.views", eventFullDto.getViews());
    }

    @Test
    @SneakyThrows
    void eventFullDtoWithNullFieldsTest() {
        eventFullDto.setCreatedOn(null);
        eventFullDto.setLocation(null);
        JsonContent<EventFullDto> result = json.write(eventFullDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", eventFullDto.getId())
                .hasJsonPathStringValue("$.description", eventFullDto.getDescription())
                .hasJsonPathStringValue("$.annotation", eventFullDto.getAnnotation())
                .hasJsonPathNumberValue("$.category.id", eventFullDto.getCategory().getId())
                .hasJsonPathStringValue("$.category.name", eventFullDto.getCategory().getName())
                .hasJsonPathNumberValue("$.confirmedRequests", eventFullDto.getConfirmedRequests())
                .hasJsonPathStringValue("$.eventDate", eventFullDto.getEventDate())
                .hasJsonPathNumberValue("$.initiator.id", eventFullDto.getInitiator().getId())
                .hasJsonPathStringValue("$.initiator.name", eventFullDto.getInitiator().getName())
                .hasJsonPathNumberValue("$.participantLimit", eventFullDto.getParticipantLimit())
                .hasJsonPathBooleanValue("$.requestModeration", eventFullDto.getRequestModeration())
                .hasJsonPathStringValue("$.state", eventFullDto.getState().name())
                .hasJsonPathBooleanValue("$.paid", eventFullDto.getPaid())
                .hasJsonPathStringValue("$.title", eventFullDto.getTitle())
                .hasJsonPathNumberValue("$.views", eventFullDto.getViews())
                .hasEmptyJsonPathValue("$.createdOn")
                .hasEmptyJsonPathValue("$.location");
    }

}