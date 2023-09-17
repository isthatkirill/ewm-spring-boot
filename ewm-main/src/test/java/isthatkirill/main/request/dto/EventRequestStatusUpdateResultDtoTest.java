package isthatkirill.main.request.dto;

import isthatkirill.main.request.model.RequestState;
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
class EventRequestStatusUpdateResultDtoTest {

    @Autowired
    private JacksonTester<EventRequestStatusUpdateResultDto> json;

    private EventRequestStatusUpdateResultDto requestResult = EventRequestStatusUpdateResultDto.builder()
            .confirmedRequests(List.of(
                    ParticipationRequestDto.builder().created(LocalDateTime.now().minusDays(1L))
                            .requester(1L).event(1L).id(1L).status(RequestState.CONFIRMED).build(),
                    ParticipationRequestDto.builder().created(LocalDateTime.now().minusDays(2L))
                            .requester(2L).event(2L).id(2L).status(RequestState.CONFIRMED).build()
            ))
            .rejectedRequests(List.of(
                    ParticipationRequestDto.builder().created(LocalDateTime.now().minusDays(3L))
                            .requester(3L).event(3L).id(3L).status(RequestState.REJECTED).build(),
                    ParticipationRequestDto.builder().created(LocalDateTime.now().minusDays(4L))
                            .requester(4L).event(4L).id(4L).status(RequestState.REJECTED).build()
            )).build();

    @Test
    @SneakyThrows
    void eventRequestStatusUpdateResultDtoTest() {
        JsonContent<EventRequestStatusUpdateResultDto> result = json.write(requestResult);

        assertThat(result)
                .hasJsonPathArrayValue("$.confirmedRequests", requestResult.getConfirmedRequests())
                .hasJsonPathArrayValue("$.rejectedRequests", requestResult.getRejectedRequests());
    }

    @Test
    @SneakyThrows
    void eventRequestStatusUpdateResultDtoWithNullFieldsTest() {
        requestResult.setConfirmedRequests(null);
        JsonContent<EventRequestStatusUpdateResultDto> result = json.write(requestResult);

        assertThat(result)
                .hasEmptyJsonPathValue("$.confirmedRequests")
                .hasJsonPathArrayValue("$.rejectedRequests", requestResult.getRejectedRequests());
    }

}