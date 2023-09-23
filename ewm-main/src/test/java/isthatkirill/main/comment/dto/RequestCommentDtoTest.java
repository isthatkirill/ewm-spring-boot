package isthatkirill.main.comment.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@JsonTest
class RequestCommentDtoTest {

    @Autowired
    private JacksonTester<RequestCommentDto> json;

    private final RequestCommentDto requestCommentDto = RequestCommentDto.builder().message("comment message").build();

    @Test
    @SneakyThrows
    void requestCommentDtoTest() {
        JsonContent<RequestCommentDto> result = json.write(requestCommentDto);

        assertThat(result)
                .hasJsonPathStringValue("$.message", requestCommentDto.getMessage());
    }

}