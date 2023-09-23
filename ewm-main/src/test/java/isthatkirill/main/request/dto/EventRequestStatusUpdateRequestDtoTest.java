package isthatkirill.main.request.dto;

import isthatkirill.main.request.model.RequestProcessedState;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@JsonTest
class EventRequestStatusUpdateRequestDtoTest {

    @Autowired
    private JacksonTester<EventRequestStatusUpdateRequestDto> json;

    private final EventRequestStatusUpdateRequestDto request = EventRequestStatusUpdateRequestDto.builder()
            .requestIds(List.of(1L, 2L, 3L))
            .status(RequestProcessedState.CONFIRMED)
            .build();

    @Test
    @SneakyThrows
    void eventRequestStatusUpdateRequestDtoTest() {
        JsonContent<EventRequestStatusUpdateRequestDto> result = json.write(request);

        assertThat(result)
                .hasJsonPathStringValue("$.status", request.getStatus().name())
                .hasJsonPathArrayValue("$.requestIds", request.getRequestIds());
    }

    @Test
    @SneakyThrows
    void eventRequestStatusUpdateRequestDtoWithNullFieldsTest() {
        request.setRequestIds(null);
        JsonContent<EventRequestStatusUpdateRequestDto> result = json.write(request);

        assertThat(result)
                .hasJsonPathStringValue("$.status", request.getStatus().name())
                .hasEmptyJsonPathValue("$.requestIds");
    }

}