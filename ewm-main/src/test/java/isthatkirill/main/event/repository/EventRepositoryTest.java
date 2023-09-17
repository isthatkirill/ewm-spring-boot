package isthatkirill.main.event.repository;

import isthatkirill.main.event.model.Event;
import isthatkirill.main.event.model.EventSort;
import isthatkirill.main.event.model.EventState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kirill Emelyanov
 */

@DataJpaTest
@Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql"})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;
    private final Pageable pageable = PageRequest.of(0, 10);

    @Test
    void findEventsByInitiatorIdTest() {
        List<Event> events = eventRepository.findEventsByInitiatorId(1L, pageable);

        assertThat(events).hasSize(5)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("java", "c++", "NASM", "Prolog", "Lisp");
    }

    @Test
    void findEventsByNonExistentInitiatorIdTest() {
        List<Event> events = eventRepository.findEventsByInitiatorId(9999L, pageable);

        assertThat(events).isEmpty();
    }

    @Test
    void findByIdAndInitiatorIdAndLockTest() {
        Optional<Event> eventOptional = eventRepository.findByIdAndInitiatorIdAndLock(1L, 1L);

        assertThat(eventOptional).isPresent()
                .get()
                .extracting(Event::getAnnotation)
                .isEqualTo("java");
    }

    @Test
    void findByNonExistentIdAndInitiatorIdAndLockTest() {
        Optional<Event> eventOptional = eventRepository.findByIdAndInitiatorIdAndLock(9999L, 1L);

        assertThat(eventOptional).isNotPresent();
    }

    @Test
    void findByIdAndLockTest() {
        Optional<Event> eventOptional = eventRepository.findByIdAndLock(2L);

        assertThat(eventOptional).isPresent()
                .get()
                .extracting(Event::getAnnotation)
                .isEqualTo("c++");
    }

    @Test
    void findByNonExistentIdAndLockTest() {
        Optional<Event> eventOptional = eventRepository.findByIdAndLock(9999L);

        assertThat(eventOptional).isNotPresent();
    }

    @Test
    void getEventIfPublishedTest() {
        Optional<Event> eventOptional = eventRepository.getEventIfPublished(2L);

        assertThat(eventOptional).isPresent()
                .get()
                .extracting(Event::getAnnotation)
                .isEqualTo("c++");
    }

    @Test
    void getPendingEventWithGetIfPublishedMethod() {
        Optional<Event> eventOptional = eventRepository.getEventIfPublished(1L);

        assertThat(eventOptional).isNotPresent();
    }

    @Test
    void findEventsByIdInTest() {
        List<Long> ids = List.of(3L, 4L, 5L);

        List<Event> events = eventRepository.findEventsByIdIn(ids);

        assertThat(events).hasSize(3)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("NASM", "Prolog", "Lisp");
    }

    @Test
    void findEventsByIdInEmptyListTest() {
        List<Long> ids = Collections.emptyList();

        List<Event> events = eventRepository.findEventsByIdIn(ids);

        assertThat(events).isEmpty();
    }

    @Test
    void findEventsByCategoryIdTest() {
        List<Event> events = eventRepository.findEventsByCategoryId(4L);

        assertThat(events).hasSize(4)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("Spring Data JPA", "Spring Security", "Spring Boot", "Spring MVC");
    }

    @Test
    void findEventsByNonExistentCategoryIdTest() {
        List<Event> events = eventRepository.findEventsByCategoryId(9999L);

        assertThat(events).isEmpty();
    }

    @Test
    void findEventByIdAndInitiatorIdTest() {
        Optional<Event> eventOptional = eventRepository.findEventByIdAndInitiatorId(1L, 1L);

        assertThat(eventOptional).isPresent()
                .get()
                .extracting(Event::getAnnotation)
                .isEqualTo("java");
    }

    @Test
    void findEventByNonExistentIdAndInitiatorIdTest() {
        Optional<Event> eventOptional = eventRepository.findEventByIdAndInitiatorId(9999L, 1L);

        assertThat(eventOptional).isNotPresent();
    }

    @Test
    void findEventsByAdminWithUsersAndStatesAndCategoriesAsParamsTest() {
        List<Long> users = List.of(1L, 5L);
        List<EventState> states = List.of(EventState.PENDING, EventState.CANCELED);
        List<Long> categories = List.of(1L, 4L);
        Integer from = 0;
        Integer size = 10;

        List<Event> events = eventRepository.findEventsByAdmin(users, states, categories, null, null, from, size);

        assertThat(events).hasSize(4)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("java", "Spring Security", "Spring Boot", "Spring MVC");
    }

    @Test
    void findEventsByAdminWithTimeIntervalTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(6).minusMinutes(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(7).plusMinutes(1L);
        Integer from = 0;
        Integer size = 10;

        List<Event> events = eventRepository.findEventsByAdmin(null, null, null, start, end, from, size);

        assertThat(events).hasSize(2)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("Spring Data JPA", "Spring Security");
    }

    @Test
    void findEventsByAdminWithPaginationTest() {
        Integer from = 6;
        Integer size = 2;

        List<Event> events = eventRepository.findEventsByAdmin(null, null, null, null, null, from, size);

        assertThat(events).hasSize(2)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("Javascript", "HTML");
    }

    @Test
    void findEventsByAdminWithAllParamsTest() {
        List<Long> users = List.of(1L, 4L, 5L);
        List<EventState> states = List.of(EventState.PENDING, EventState.PUBLISHED);
        List<Long> categories = List.of(2L, 4L);
        LocalDateTime start = LocalDateTime.now().plusDays(5).minusMinutes(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(7).plusMinutes(1L);
        Integer from = 1;
        Integer size = 2;

        List<Event> events = eventRepository.findEventsByAdmin(users, states, categories, start, end, from, size);

        assertThat(events).hasSize(2)
                .extracting(Event::getAnnotation)
                .containsExactlyInAnyOrder("Lisp", "Spring Data JPA");
    }

    @Test
    void findEventsByPublicWithTextTest() {
        String text = "Conference";
        Integer from = 0;
        Integer size = 10;
        EventSort sort = EventSort.EVENT_DATE;

        List<Event> events = eventRepository.findEventsByPublic(text, null, null, null, null, from, size, sort);

        assertThat(events).hasSize(6)
                .extracting(Event::getAnnotation)
                .containsExactly("c++", "Prolog", "CSS", "Javascript", "HTML", "Spring Data JPA");
    }

    @Test
    void tryToFindNonPublishedEventsByPublicWithTextTest() {
        String text = "Spring Sec";
        Integer from = 0;
        Integer size = 10;
        EventSort sort = EventSort.EVENT_DATE;

        List<Event> events = eventRepository.findEventsByPublic(text, null, null, null, null, from, size, sort);

        assertThat(events).isEmpty();
    }

    @Test
    void findEventsByPublicWithPaidAndCategoriesTest() {
        List<Long> categories = List.of(1L, 2L, 3L);
        Integer from = 0;
        Integer size = 10;
        boolean paid = true;
        EventSort sort = EventSort.EVENT_DATE;

        List<Event> events = eventRepository.findEventsByPublic(null, categories, paid, null, null, from, size, sort);

        assertThat(events).hasSize(1)
                .extracting(Event::getAnnotation)
                .containsExactly("c++");
    }

    @Test
    void findEventsByPublicWithTimeIntervalTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(4).minusMinutes(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(4).plusMinutes(1L);
        Integer from = 0;
        Integer size = 10;
        EventSort sort = EventSort.EVENT_DATE;

        List<Event> events = eventRepository.findEventsByPublic(null, null, null, start, end, from, size, sort);

        assertThat(events).hasSize(1)
                .extracting(Event::getAnnotation)
                .containsExactly("c++");
    }

    @Test
    void findEventsByPublicWithAllParamsTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(5).minusMinutes(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(5).plusMinutes(1L);
        Integer from = 0;
        Integer size = 1;
        List<Long> categories = List.of(3L);
        String text = "Conf";
        boolean paid = false;
        EventSort sort = EventSort.EVENT_DATE;

        List<Event> events = eventRepository.findEventsByPublic(text, categories, paid, start, end, from, size, sort);

        assertThat(events).hasSize(1)
                .extracting(Event::getAnnotation)
                .containsExactly("CSS");
    }

}