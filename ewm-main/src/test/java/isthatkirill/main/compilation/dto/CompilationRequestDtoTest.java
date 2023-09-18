package isthatkirill.main.compilation.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Kirill Emelyanov
 */

@JsonTest
class CompilationRequestDtoTest {

    @Autowired
    private JacksonTester<CompilationRequestDto> json;

    private CompilationRequestDto compilationRequestDto = CompilationRequestDto.builder()
            .events(List.of(1L, 2L))
            .title("title")
            .pinned(true)
            .build();

    @Test
    @SneakyThrows
    void compilationRequestDtoTest() {
        JsonContent<CompilationRequestDto> result = json.write(compilationRequestDto);

        assertThat(result)
                .hasJsonPathStringValue("title", compilationRequestDto.getTitle())
                .hasJsonPathArrayValue("events", compilationRequestDto.getEvents())
                .hasJsonPathBooleanValue("pinned", compilationRequestDto.getPinned());
    }

    @Test
    @SneakyThrows
    void compilationRequestDtoWithNullFieldsTest() {
        compilationRequestDto.setPinned(null);
        compilationRequestDto.setEvents(null);
        JsonContent<CompilationRequestDto> result = json.write(compilationRequestDto);

        assertThat(result)
                .hasJsonPathStringValue("title", compilationRequestDto.getTitle())
                .hasEmptyJsonPathValue("events")
                .hasEmptyJsonPathValue("pinned");
    }


}