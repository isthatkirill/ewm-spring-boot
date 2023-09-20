package isthatkirill.main.compilation.dto;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.user.dto.UserShortDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@JsonTest
class CompilationResponseDtoTest {

    @Autowired
    private JacksonTester<CompilationResponseDto> json;

    private final CompilationResponseDto compilationResponseDto = CompilationResponseDto.builder()
            .id(1L)
            .events(List.of(
                    EventShortDto.builder()
                            .annotation("annotation")
                            .eventDate(LocalDateTime.now().plusHours(4))
                            .category(CategoryDto.builder().id(1L).name("cat_name").build())
                            .paid(false)
                            .confirmedRequests(200L)
                            .views(200L)
                            .initiator(UserShortDto.builder().id(1L).name("user_name").build())
                            .title("title")
                            .build()
            ))
            .pinned(true)
            .title("title")
            .build();

    @Test
    @SneakyThrows
    void compilationResponseDtoTest() {
        JsonContent<CompilationResponseDto> result = json.write(compilationResponseDto);

        assertThat(result)
                .hasJsonPathArrayValue("events", compilationResponseDto.getEvents())
                .hasJsonPathNumberValue("id", compilationResponseDto.getId())
                .hasJsonPathStringValue("title", compilationResponseDto.getTitle())
                .hasJsonPathBooleanValue("pinned", compilationResponseDto.getPinned());
    }

    @Test
    @SneakyThrows
    void compilationResponseDtoWithNullFieldsTest() {
        compilationResponseDto.setPinned(null);
        JsonContent<CompilationResponseDto> result = json.write(compilationResponseDto);

        assertThat(result)
                .hasJsonPathArrayValue("events", compilationResponseDto.getEvents())
                .hasJsonPathNumberValue("id", compilationResponseDto.getId())
                .hasJsonPathStringValue("title", compilationResponseDto.getTitle())
                .hasEmptyJsonPathValue("pinned");
    }

}