package isthatkirill.main.request.dto;

import isthatkirill.main.request.model.RequestState;
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
class ParticipationRequestDtoTest {

    @Autowired
    private JacksonTester<ParticipationRequestDto> json;

    private final ParticipationRequestDto participationRequest = ParticipationRequestDto.builder()
            .id(1L)
            .requester(1L)
            .event(1L)
            .created(LocalDateTime.now())
            .status(RequestState.PENDING)
            .build();

    @Test
    @SneakyThrows
    void participationRequestDtoTest() {
        JsonContent<ParticipationRequestDto> result = json.write(participationRequest);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", participationRequest.getId())
                .hasJsonPathNumberValue("$.requester", participationRequest.getRequester())
                .hasJsonPathNumberValue("$.event", participationRequest.getEvent())
                .hasJsonPathStringValue("$.status", participationRequest.getStatus().name())
                .hasJsonPathStringValue("$.created", participationRequest.getCreated());
    }

    @Test
    @SneakyThrows
    void participationRequestDtoWithNullFieldsTest() {
        participationRequest.setCreated(null);
        JsonContent<ParticipationRequestDto> result = json.write(participationRequest);

        assertThat(result)
                .hasJsonPathNumberValue("$.id", participationRequest.getId())
                .hasJsonPathNumberValue("$.requester", participationRequest.getRequester())
                .hasJsonPathNumberValue("$.event", participationRequest.getEvent())
                .hasJsonPathStringValue("$.status", participationRequest.getStatus().name())
                .hasEmptyJsonPathValue("$.created");
    }

}