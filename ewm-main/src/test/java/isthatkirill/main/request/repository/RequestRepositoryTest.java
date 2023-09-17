package isthatkirill.main.request.repository;

import isthatkirill.main.event.model.EventConfirmedRequests;
import isthatkirill.main.request.model.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@DataJpaTest
@Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql", "/testdata/test-requests.sql"})
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void findRequestByRequesterIdAndEventIdTest() {
        Long userId = 1L;
        Long eventId = 6L;

        Optional<Request> optionalRequest = requestRepository.findRequestByRequesterIdAndEventId(userId, eventId);

        assertThat(optionalRequest).isPresent();

        Request request = optionalRequest.get();

        assertThat(request)
                .hasFieldOrPropertyWithValue("id", 1L)
                .extracting(Request::getRequester)
                .hasFieldOrPropertyWithValue("name", "katya")
                .hasFieldOrPropertyWithValue("id", userId);

        assertThat(request)
                .extracting(Request::getEvent)
                .hasFieldOrPropertyWithValue("annotation", "CSS")
                .hasFieldOrPropertyWithValue("id", eventId);
    }

    @Test
    void findRequestsByRequesterIdTest() {
        Long userId = 2L;

        List<Request> requests = requestRepository.findRequestsByRequesterId(userId);

        assertThat(requests).hasSize(2)
                .extracting(Request::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void findRequestsByRequesterIdAndId() {
        Long userId = 2L;
        Long requestId = 3L;

        Optional<Request> optionalRequest = requestRepository.findRequestsByRequesterIdAndId(userId, requestId);

        assertThat(optionalRequest).isPresent();

        Request request = optionalRequest.get();

        assertThat(request)
                .hasFieldOrPropertyWithValue("id", requestId)
                .extracting(Request::getRequester)
                .hasFieldOrPropertyWithValue("name", "vasya")
                .hasFieldOrPropertyWithValue("id", userId);

        assertThat(request)
                .extracting(Request::getEvent)
                .hasFieldOrPropertyWithValue("annotation", "c++")
                .hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    void findRequestsByIdInTest() {
        List<Long> ids = List.of(1L, 2L, 3L);

        List<Request> requests = requestRepository.findRequestsByIdIn(ids);

        assertThat(requests).hasSize(3)
                .extracting(Request::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    void findRequestsByIdInListWithNonExistentIdsTest() {
        List<Long> ids = List.of(101L, 102L, 103L);

        List<Request> requests = requestRepository.findRequestsByIdIn(ids);

        assertThat(requests).isEmpty();
    }

    @Test
    void findRequestsByEventInitiatorIdAndEventId() {
        Long userId = 4L;
        Long eventId = 8L;

        List<Request> requests = requestRepository.findRequestsByEventInitiatorIdAndEventId(userId, eventId);

        assertThat(requests).hasSize(3)
                .extracting(Request::getId)
                .containsExactlyInAnyOrder(5L, 6L, 7L);
    }

    @Test
    void getConfirmedRequestsTest() {
        Long eventId = 8L;

        Long quantity = requestRepository.getConfirmedRequests(eventId);

        assertThat(quantity).isNotNull()
                .isEqualTo(2L);
    }

    @Test
    void getConfirmedRequestsAsListTest() {
        List<Long> ids = List.of(2L, 6L);

        List<EventConfirmedRequests> confirmedRequests = requestRepository.getConfirmedRequests(ids);

        assertThat(confirmedRequests).hasSize(1)
                .extracting(EventConfirmedRequests::getConfirmed)
                .containsExactlyInAnyOrder(2L);
    }
}