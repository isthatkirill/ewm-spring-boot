package isthatkirill.main.event.dto;

import isthatkirill.main.category.dto.CategoryDto;
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
class EventShortDtoTest {

    @Autowired
    private JacksonTester<EventShortDto> json;

    private final EventShortDto eventShortDto = EventShortDto.builder()
            .id(1L)
            .annotation("eventShortDto_annotation")
            .category(CategoryDto.builder()
                    .id(1L)
                    .name("category_name")
                    .build())
            .confirmedRequests(100L)
            .eventDate(LocalDateTime.now().plusHours(3))
            .initiator(UserShortDto.builder()
                    .id(1L)
                    .name("user_name")
                    .build())
            .paid(true)
            .title("eventShortDto_title")
            .views(100L)
            .build();

    @Test
    @SneakyThrows
    void eventShortDtoTest() {
        JsonContent<EventShortDto> result = json.write(eventShortDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", eventShortDto.getId())
                .hasJsonPathStringValue("$.annotation", eventShortDto.getAnnotation())
                .hasJsonPathNumberValue("$.category.id", eventShortDto.getCategory().getId())
                .hasJsonPathStringValue("$.category.name", eventShortDto.getCategory().getName())
                .hasJsonPathNumberValue("$.confirmedRequests", eventShortDto.getConfirmedRequests())
                .hasJsonPathStringValue("$.eventDate", eventShortDto.getEventDate())
                .hasJsonPathNumberValue("$.initiator.id", eventShortDto.getInitiator().getId())
                .hasJsonPathStringValue("$.initiator.name", eventShortDto.getInitiator().getName())
                .hasJsonPathBooleanValue("$.paid", eventShortDto.getPaid())
                .hasJsonPathStringValue("$.title", eventShortDto.getTitle())
                .hasJsonPathNumberValue("$.views", eventShortDto.getViews());
    }

    @Test
    @SneakyThrows
    void eventShortDtoWithNullFieldsTest() {
        eventShortDto.setInitiator(null);
        eventShortDto.setCategory(null);
        eventShortDto.setEventDate(null);
        JsonContent<EventShortDto> result = json.write(eventShortDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", eventShortDto.getId())
                .hasJsonPathStringValue("$.annotation", eventShortDto.getAnnotation())
                .hasJsonPathNumberValue("$.confirmedRequests", eventShortDto.getConfirmedRequests())
                .hasJsonPathBooleanValue("$.paid", eventShortDto.getPaid())
                .hasJsonPathStringValue("$.title", eventShortDto.getTitle())
                .hasJsonPathNumberValue("$.views", eventShortDto.getViews())
                .hasEmptyJsonPathValue("$.category")
                .hasEmptyJsonPathValue("$.eventDate")
                .hasEmptyJsonPathValue("$.initiator");
    }

}