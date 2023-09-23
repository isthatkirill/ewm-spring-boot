package isthatkirill.main.request.service;

import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.error.exception.ForbiddenException;
import isthatkirill.main.request.dto.EventRequestStatusUpdateRequestDto;
import isthatkirill.main.request.dto.EventRequestStatusUpdateResultDto;
import isthatkirill.main.request.dto.ParticipationRequestDto;
import isthatkirill.main.request.model.RequestProcessedState;
import isthatkirill.main.request.model.RequestState;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Kirill Emelyanov
 */

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RequestServiceImplTest {

    @Autowired
    private RequestService requestService;

    //current requestId = 8 --> test-requests.sql contains previous 7 requests

    @Test
    @Order(1)
    @Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql", "/testdata/test-requests.sql"})
    void createRequestOnNonRequiredModerationEventTest() {
        Long userId = 4L;
        Long eventId = 7L;
        Long requestId = 8L;

        ParticipationRequestDto requestDto = requestService.create(userId, eventId);

        assertThat(requestDto).isNotNull()
                .hasFieldOrPropertyWithValue("event", eventId)
                .hasFieldOrPropertyWithValue("id", requestId)
                .hasFieldOrPropertyWithValue("requester", userId)
                .hasFieldOrPropertyWithValue("status", RequestState.CONFIRMED);
    }

    @Test
    @Order(2)
    void createRequestOnRequiredModerationEventTest() {
        Long userId = 5L;
        Long eventId = 9L;
        Long requestId = 9L;

        ParticipationRequestDto requestDto = requestService.create(userId, eventId);

        assertThat(requestDto).isNotNull()
                .hasFieldOrPropertyWithValue("event", eventId)
                .hasFieldOrPropertyWithValue("id", requestId)
                .hasFieldOrPropertyWithValue("requester", userId)
                .hasFieldOrPropertyWithValue("status", RequestState.PENDING);
    }

    @Test
    @Order(3)
    void createDuplicateRequestTest() {
        Long userId = 4L;
        Long eventId = 7L;

        assertThrows(ForbiddenException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    @Order(4)
    void createRequestWithEventExceededLimitTest() {
        Long userId = 4L;
        Long eventId = 2L;

        assertThrows(ForbiddenException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    @Order(5)
    void createRequestByNonExistentUserTest() {
        Long userId = Long.MAX_VALUE;
        Long eventId = 2L;

        assertThrows(EntityNotFoundException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    @Order(6)
    void createRequestOnNonExistentEventTest() {
        Long userId = 4L;
        Long eventId = Long.MAX_VALUE;

        assertThrows(EntityNotFoundException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    @Order(7)
    void createRequestOnNonPublishedEventTest() {
        Long userId = 4L;
        Long eventId = 12L;

        assertThrows(ForbiddenException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    @Order(8)
    void createRequestOnOwnEventEventTest() {
        Long userId = 1L;
        Long eventId = 1L;

        assertThrows(ForbiddenException.class, () -> requestService.create(userId, eventId));
    }

    @Test
    @Order(9)
    void getRequestsByUserIdTest() {
        Long userId = 2L;

        List<ParticipationRequestDto> requests = requestService.getRequestsByUserId(userId);

        assertThat(requests).hasSize(2)
                .extracting(ParticipationRequestDto::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    @Order(10)
    void getRequestsByNonExistentUserIdTest() {
        Long userId = Long.MAX_VALUE;

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestsByUserId(userId));
    }

    @Test
    @Order(11)
    void cancelTest() {
        Long userId = 4L;
        Long eventId = 7L;
        Long requestId = 8L;

        ParticipationRequestDto request = requestService.cancel(userId, requestId);

        assertThat(request).isNotNull()
                .hasFieldOrPropertyWithValue("event", eventId)
                .hasFieldOrPropertyWithValue("id", requestId)
                .hasFieldOrPropertyWithValue("requester", userId)
                .hasFieldOrPropertyWithValue("status", RequestState.CANCELED);
    }

    @Test
    @Order(12)
    void cancelByNonExistentUser() {
        Long userId = Long.MAX_VALUE;
        Long requestId = 8L;

        assertThrows(EntityNotFoundException.class, () -> requestService.cancel(userId, requestId));
    }

    @Test
    @Order(13)
    void processConfirmRequestsByInitiatorTest() {
        Long userId = 4L;
        Long eventId = 9L;
        Long requestId = 9L;

        EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
                .requestIds(List.of(requestId))
                .status(RequestProcessedState.CONFIRMED)
                .build();

        EventRequestStatusUpdateResultDto resultDto = requestService.processRequestsByInitiator(updateRequest, userId, eventId);

        assertThat(resultDto).isNotNull();

        List<ParticipationRequestDto> confirmedRequests = resultDto.getConfirmedRequests();
        List<ParticipationRequestDto> rejectedRequests = resultDto.getRejectedRequests();

        assertThat(confirmedRequests).hasSize(1)
                .extracting(ParticipationRequestDto::getId)
                .containsExactlyInAnyOrder(requestId);

        assertThat(rejectedRequests).isEmpty();
    }

    @Test
    @Order(14)
    void processRejectRequestsByInitiatorTest() {
        Long userId = 1L;
        Long eventId = 1L;
        List<Long> ids = List.of(1L, 2L);

        EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
                .requestIds(ids)
                .status(RequestProcessedState.REJECTED)
                .build();

        EventRequestStatusUpdateResultDto resultDto = requestService.processRequestsByInitiator(updateRequest, userId, eventId);

        assertThat(resultDto).isNotNull();

        List<ParticipationRequestDto> confirmedRequests = resultDto.getConfirmedRequests();
        List<ParticipationRequestDto> rejectedRequests = resultDto.getRejectedRequests();

        assertThat(confirmedRequests).isEmpty();

        assertThat(rejectedRequests).hasSize(2)
                .extracting(ParticipationRequestDto::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @Order(15)
    void processRequestsWithNonExistentRequestIdByInitiatorTest() {
        Long userId = 4L;
        Long eventId = 9L;
        Long requestId = 9L;
        Long nonExistentRequestId = Long.MAX_VALUE;

        EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
                .requestIds(List.of(requestId, nonExistentRequestId))
                .status(RequestProcessedState.CONFIRMED)
                .build();

        assertThrows(EntityNotFoundException.class, () -> requestService.processRequestsByInitiator(updateRequest, userId, eventId));
    }

    @Test
    @Order(16)
    void tryToRejectAlreadyRejectedRequestsByInitiatorTest() {
        Long userId = 1L;
        Long eventId = 1L;
        List<Long> ids = List.of(1L, 2L);

        EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
                .requestIds(ids)
                .status(RequestProcessedState.REJECTED)
                .build();

        assertThrows(ForbiddenException.class, () -> requestService.processRequestsByInitiator(updateRequest, userId, eventId));
    }

    @Test
    @Order(17)
    void processRequestsByNonExistentInitiatorTest() {
        Long userId = Long.MAX_VALUE;
        Long eventId = 2L;
        List<Long> ids = List.of(3L, 4L);

        EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
                .requestIds(ids)
                .status(RequestProcessedState.CONFIRMED)
                .build();

        assertThrows(EntityNotFoundException.class, () -> requestService.processRequestsByInitiator(updateRequest, userId, eventId));
    }

    @Test
    @Order(18)
    void processRequestsOnNonExistentEventByInitiatorTest() {
        Long userId = 1L;
        Long eventId = Long.MAX_VALUE;
        List<Long> ids = List.of(3L, 4L);

        EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
                .requestIds(ids)
                .status(RequestProcessedState.CONFIRMED)
                .build();

        assertThrows(EntityNotFoundException.class, () -> requestService.processRequestsByInitiator(updateRequest, userId, eventId));
    }

    @Test
    @Order(19)
    void getRequestsByInitiatorTest() {
        Long userId = 4L;
        Long eventId = 8L;

        List<ParticipationRequestDto> requests = requestService.getRequestsByInitiator(userId, eventId);

        assertThat(requests).hasSize(3)
                .extracting(ParticipationRequestDto::getId)
                .containsExactlyInAnyOrder(5L, 6L, 7L);
    }

    @Test
    @Order(20)
    void getRequestsByInitiatorShouldEmptyListTest() {
        Long userId = 5L;
        Long eventId = 10L;

        List<ParticipationRequestDto> requests = requestService.getRequestsByInitiator(userId, eventId);

        assertThat(requests).isEmpty();
    }
}