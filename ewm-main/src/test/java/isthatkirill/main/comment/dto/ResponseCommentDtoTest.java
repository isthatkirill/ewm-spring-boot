package isthatkirill.main.comment.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@JsonTest
class ResponseCommentDtoTest {

    @Autowired
    private JacksonTester<ResponseCommentDto> json;

    private final ResponseCommentDto responseCommentDto = ResponseCommentDto.builder()
            .id(1L)
            .authorId(1L)
            .message("comment message")
            .created(LocalDateTime.now().minusDays(1))
            .build();

    @Test
    @SneakyThrows
    void responseCommentDtoTest() {
        JsonContent<ResponseCommentDto> result = json.write(responseCommentDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", responseCommentDto.getId())
                .hasJsonPathNumberValue("$.authorId", responseCommentDto.getAuthorId())
                .hasJsonPathStringValue("$.message", responseCommentDto.getMessage())
                .hasJsonPathStringValue("$.created", responseCommentDto.getCreated());
    }

    @Test
    @SneakyThrows
    void responseCommentDtoWithNullFieldsTest() {
        responseCommentDto.setCreated(null);
        responseCommentDto.setAuthorId(null);
        JsonContent<ResponseCommentDto> result = json.write(responseCommentDto);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", responseCommentDto.getId())
                .hasEmptyJsonPathValue("$.authorId")
                .hasJsonPathStringValue("$.message", responseCommentDto.getMessage())
                .hasEmptyJsonPathValue("$.created");
    }

}