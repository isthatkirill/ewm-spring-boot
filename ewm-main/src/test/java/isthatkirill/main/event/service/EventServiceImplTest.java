package isthatkirill.main.event.service;

import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.error.exception.ForbiddenException;
import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.dto.NewEventDto;
import isthatkirill.main.event.dto.UpdateEventDto;
import isthatkirill.main.event.model.EventSort;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.model.EventStateAction;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.request.repository.RequestRepository;
import isthatkirill.main.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Kirill Emelyanov
 */

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventServiceImplTest {

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private StatServiceImpl statService;

    @MockBean
    private RequestRepository requestRepository;

    private Long currentEventId = 13L; //test-events.sql contains previous 12 events
    private final Long mockViewsRequests = 0L;

    @BeforeAll
    public void configureMock() {
        when(statService.getViews(anyList())).thenReturn(Collections.emptyMap());
        when(statService.getConfirmedRequests(anyList())).thenReturn(Collections.emptyMap());
    }

    @Test
    @Order(1)
    @Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql"})
    void createTest() {
        Long userId = 1L;
        Long categoryId = 1L;

        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("event_created_via_eventService")
                .description("description")
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(4))
                .location(LocationDto.builder().lat(15.15f).lon(15.15f).build())
                .paid(false)
                .participantLimit(2)
                .requestModeration(false)
                .title("title")
                .build();

        EventFullDto eventFullDto = eventService.create(newEventDto, userId);

        assertThat(eventFullDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", currentEventId)
                .hasFieldOrPropertyWithValue("description", newEventDto.getDescription())
                .hasFieldOrPropertyWithValue("annotation", newEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("category.id", newEventDto.getCategory())
                .hasFieldOrPropertyWithValue("confirmedRequests", mockViewsRequests)
                .hasFieldOrPropertyWithValue("views", mockViewsRequests)
                .hasFieldOrPropertyWithValue("eventDate", newEventDto.getEventDate())
                .hasFieldOrProperty("createdOn").isNotNull()
                .hasFieldOrPropertyWithValue("location.lat", newEventDto.getLocation().getLat())
                .hasFieldOrPropertyWithValue("location.lon", newEventDto.getLocation().getLon())
                .hasFieldOrPropertyWithValue("initiator.id", userId)
                .hasFieldOrPropertyWithValue("participantLimit", newEventDto.getParticipantLimit())
                .hasFieldOrPropertyWithValue("requestModeration", newEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("state", EventState.PENDING)
                .hasFieldOrPropertyWithValue("paid", newEventDto.getPaid())
                .hasFieldOrPropertyWithValue("title", newEventDto.getTitle());

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());

        currentEventId++;
    }

    @Test
    @Order(2)
    void createByNonExistentUserTest() {
        Long userId = 999L;
        Long categoryId = 1L;

        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("by_non_existent_user")
                .description("description")
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(4))
                .location(LocationDto.builder().lat(15.15f).lon(15.15f).build())
                .paid(false)
                .participantLimit(2)
                .requestModeration(false)
                .title("title")
                .build();

        assertThrows(EntityNotFoundException.class, () -> eventService.create(newEventDto, userId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());

        currentEventId++;
    }

    @Test
    @Order(3)
    void createWithNonExistentCategoryTest() {
        Long userId = 1L;
        Long categoryId = 999L;

        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("by_non_existent_user")
                .description("description")
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(4))
                .location(LocationDto.builder().lat(15.15f).lon(15.15f).build())
                .paid(false)
                .participantLimit(2)
                .requestModeration(false)
                .title("title")
                .build();

        assertThrows(EntityNotFoundException.class, () -> eventService.create(newEventDto, userId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());

        currentEventId++;
    }

    @Test
    @Order(4)
    void getAllByInitiatorIdTest() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        List<EventShortDto> events = eventService.getAllByInitiatorId(userId, from, size);

        assertThat(events).hasSize(6)
                .extracting(EventShortDto::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 13L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(5)
    void getAllByInitiatorIdWithPaginationTest() {
        Long userId = 1L;
        Integer from = 2;
        Integer size = 2;

        List<EventShortDto> events = eventService.getAllByInitiatorId(userId, from, size);

        assertThat(events).hasSize(2)
                .extracting(EventShortDto::getId)
                .containsExactlyInAnyOrder(3L, 4L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(6)
    void getAllByNonExistentInitiatorTest() {
        Long userId = 999L;
        Integer from = 0;
        Integer size = 10;

        List<EventShortDto> events = eventService.getAllByInitiatorId(userId, from, size);

        assertThat(events).isEmpty();

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(7)
    void updateByInitiatorTest() {
        Long userId = 1L;
        Long eventId = 13L;
        Long categoryId = 2L;

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .annotation("updated")
                .description("description updated")
                .eventDate(LocalDateTime.now().plusHours(10))
                .category(categoryId)
                .location(LocationDto.builder().lon(16.16f).lat(16.16f).build())
                .paid(false)
                .participantLimit(6)
                .requestModeration(false)
                .title("title updated")
                .stateAction(EventStateAction.CANCEL_REVIEW)
                .build();

        EventFullDto eventFullDto = eventService.updateByInitiator(updateEventDto, eventId, userId);

        assertThat(eventFullDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", eventId)
                .hasFieldOrPropertyWithValue("description", updateEventDto.getDescription())
                .hasFieldOrPropertyWithValue("annotation", updateEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("category.id", updateEventDto.getCategory())
                .hasFieldOrPropertyWithValue("confirmedRequests", mockViewsRequests)
                .hasFieldOrPropertyWithValue("views", mockViewsRequests)
                .hasFieldOrPropertyWithValue("eventDate", updateEventDto.getEventDate())
                .hasFieldOrProperty("createdOn").isNotNull()
                .hasFieldOrPropertyWithValue("location.lat", updateEventDto.getLocation().getLat())
                .hasFieldOrPropertyWithValue("location.lon", updateEventDto.getLocation().getLon())
                .hasFieldOrPropertyWithValue("initiator.id", userId)
                .hasFieldOrPropertyWithValue("participantLimit", updateEventDto.getParticipantLimit())
                .hasFieldOrPropertyWithValue("requestModeration", updateEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("state", EventState.CANCELED)
                .hasFieldOrPropertyWithValue("paid", updateEventDto.getPaid())
                .hasFieldOrPropertyWithValue("title", updateEventDto.getTitle());

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(8)
    void updateNotOwnEventTest() {
        Long userId = 2L;
        Long eventId = 13L;
        Long categoryId = 2L;

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .annotation("updated")
                .description("description updated")
                .eventDate(LocalDateTime.now().plusHours(10))
                .category(categoryId)
                .location(LocationDto.builder().lon(16.16f).lat(16.16f).build())
                .paid(false)
                .participantLimit(6)
                .requestModeration(false)
                .title("title updated")
                .stateAction(EventStateAction.CANCEL_REVIEW)
                .build();

        assertThrows(EntityNotFoundException.class, () -> eventService.updateByInitiator(updateEventDto, eventId, userId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(9)
    void updateByInitiatorWithNonExistentEventTest() {
        Long userId = 1L;
        Long eventId = 13L;
        Long categoryId = 999L;

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .annotation("updated")
                .description("description updated")
                .eventDate(LocalDateTime.now().plusHours(10))
                .category(categoryId)
                .location(LocationDto.builder().lon(16.16f).lat(16.16f).build())
                .paid(false)
                .participantLimit(6)
                .requestModeration(false)
                .title("title updated")
                .stateAction(EventStateAction.CANCEL_REVIEW)
                .build();

        assertThrows(EntityNotFoundException.class, () -> eventService.updateByInitiator(updateEventDto, eventId, userId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(10)
    void updateByAdminTest() {
        Long categoryId = 1L;
        Long eventId = 1L;
        Long userId = 1L;

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .annotation("java updated")
                .description("java conference updated")
                .eventDate(LocalDateTime.now().plusDays(2))
                .category(categoryId)
                .location(LocationDto.builder().lon(30.30f).lat(30.30f).build())
                .paid(true)
                .participantLimit(4)
                .requestModeration(true)
                .title("java conference title updated")
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .build();

        EventFullDto eventFullDto = eventService.updateByAdmin(updateEventDto, eventId);

        assertThat(eventFullDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", eventId)
                .hasFieldOrPropertyWithValue("description", updateEventDto.getDescription())
                .hasFieldOrPropertyWithValue("annotation", updateEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("category.id", updateEventDto.getCategory())
                .hasFieldOrPropertyWithValue("confirmedRequests", mockViewsRequests)
                .hasFieldOrPropertyWithValue("views", mockViewsRequests)
                .hasFieldOrPropertyWithValue("eventDate", updateEventDto.getEventDate())
                .hasFieldOrProperty("createdOn").isNotNull()
                .hasFieldOrPropertyWithValue("location.lat", updateEventDto.getLocation().getLat())
                .hasFieldOrPropertyWithValue("location.lon", updateEventDto.getLocation().getLon())
                .hasFieldOrPropertyWithValue("initiator.id", userId)
                .hasFieldOrPropertyWithValue("participantLimit", updateEventDto.getParticipantLimit())
                .hasFieldOrPropertyWithValue("requestModeration", updateEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("state", EventState.PUBLISHED)
                .hasFieldOrPropertyWithValue("paid", updateEventDto.getPaid())
                .hasFieldOrPropertyWithValue("title", updateEventDto.getTitle());

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(10)
    void updateNonExistentEventByAdminTest() {
        Long categoryId = 1L;
        Long eventId = 999L;

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .annotation("java updated")
                .description("java conference updated")
                .eventDate(LocalDateTime.now().plusDays(2))
                .category(categoryId)
                .location(LocationDto.builder().lon(30.30f).lat(30.30f).build())
                .paid(true)
                .participantLimit(4)
                .requestModeration(true)
                .title("java conference title updated")
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .build();

        assertThrows(EntityNotFoundException.class, () -> eventService.updateByAdmin(updateEventDto, eventId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(11)
    void updateEventByAdminWithInvalidParticipantLimitTest() {
        when(requestRepository.getConfirmedRequests(anyLong())).thenReturn(4L);

        Long categoryId = 1L;
        Long eventId = 1L;

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .annotation("java updated")
                .description("java conference updated")
                .eventDate(LocalDateTime.now().plusDays(2))
                .category(categoryId)
                .location(LocationDto.builder().lon(30.30f).lat(30.30f).build())
                .paid(true)
                .participantLimit(2)
                .requestModeration(true)
                .title("java conference title updated")
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .build();

        assertThrows(ForbiddenException.class, () -> eventService.updateByAdmin(updateEventDto, eventId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(12)
    void getAllEventsByAdminWithTimeIntervalTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(6).minusMinutes(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(7).plusMinutes(1L);
        Integer from = 0;
        Integer size = 10;

        List<EventFullDto> events = eventService.getAllEventsByAdmin(null, null, null, start, end, from, size);

        assertThat(events).hasSize(2)
                .extracting(EventShortDto::getId)
                .containsExactlyInAnyOrder(9L, 10L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(13)
    void getAllEventsByAdminWithUsersAndStatesTest() {
        List<EventState> states = List.of(EventState.PUBLISHED);
        List<Long> userIds = List.of(1L, 2L);
        Integer from = 0;
        Integer size = 10;

        List<EventFullDto> events = eventService.getAllEventsByAdmin(userIds, states, null, null, null, from, size);

        assertThat(events).hasSize(4)
                .extracting(EventShortDto::getId)
                .containsExactlyInAnyOrder(1L, 2L, 4L, 6L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(14)
    void getAllEventsByAdminWithAllParamsTest() {
        List<Long> userIds = List.of(1L, 4L, 5L);
        List<EventState> states = List.of(EventState.PENDING, EventState.PUBLISHED);
        List<Long> categories = List.of(2L, 4L);
        LocalDateTime start = LocalDateTime.now().plusDays(5).minusMinutes(1L);
        LocalDateTime end = LocalDateTime.now().plusDays(7).plusMinutes(1L);
        Integer from = 0;
        Integer size = 1;

        List<EventFullDto> events = eventService.getAllEventsByAdmin(userIds, states, categories, start, end, from, size);

        assertThat(events).hasSize(1)
                .extracting(EventShortDto::getId)
                .containsExactlyInAnyOrder(4L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(15)
    void getAllEventsByPublicWithTextTest() {
        String text = "Conference";
        String uri = "/events";
        String ip = "192.168.1.1";
        Integer from = 0;
        Integer size = 10;
        EventSort sort = EventSort.EVENT_DATE;


        List<EventShortDto> events = eventService.getAllEventsByPublic(text, null, null, null, null,
                true, sort, from, size, uri, ip);

        assertThat(events).hasSize(6)
                .extracting(EventShortDto::getId)
                .containsExactly(1L, 2L, 4L, 6L, 7L, 8L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
        verify(statService, times(1)).hit(uri, ip);
    }

    @Test
    @Order(16)
    void getAllEventsByPublicSortingByViewsTest() {
        Map<Long, Long> views = Map.of(
                1L, 3L,
                2L, 1L,
                4L, 8L,
                6L, 2L,
                7L, 10L,
                8L, 15L);

        when(statService.getViews(any())).thenReturn(views);

        String uri = "/events";
        String ip = "192.168.1.1";
        String text = "Conference";
        Integer from = 0;
        Integer size = 10;
        EventSort sort = EventSort.VIEWS;

        List<EventShortDto> events = eventService.getAllEventsByPublic(text, null, null, null, null,
                false, sort, from, size, uri, ip);

        assertThat(events).hasSize(6)
                .extracting(EventShortDto::getId)
                .containsExactly(2L, 6L, 1L, 4L, 7L, 8L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
        verify(statService, times(1)).hit(uri, ip);
    }

    @Test
    @Order(17)
    void getEventByPublicTest() {
        Long eventId = 1L;
        String uri = "/events/1";
        String ip = "192.168.1.1";

        EventFullDto eventFullDto = eventService.getEventByPublic(eventId, uri, ip);

        assertThat(eventFullDto).isNotNull()
                .extracting(EventShortDto::getAnnotation)
                .isEqualTo("java updated");

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
        verify(statService, times(1)).hit(uri, ip);
    }

    @Test
    @Order(18)
    void getNonPublishedEventByPublicTest() {
        Long eventId = 5L;
        String uri = "/events/5";
        String ip = "192.168.1.1";

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventByPublic(eventId, uri, ip));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
        verify(statService, never()).hit(uri, ip);
    }

    @Test
    @Order(19)
    void getNonExistentEventByPublicTest() {
        Long eventId = 999L;
        String uri = "/events/999";
        String ip = "192.168.1.1";

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventByPublic(eventId, uri, ip));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
        verify(statService, never()).hit(uri, ip);
    }

    @Test
    @Order(20)
    void getEventByIdAndInitiatorIdTest() {
        Long userId = 1L;
        Long eventId = 1L;

        EventFullDto eventFullDto = eventService.getEventByIdAndInitiatorId(eventId, userId);

        assertThat(eventFullDto).isNotNull()
                .extracting(EventShortDto::getAnnotation)
                .isEqualTo("java updated");

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(21)
    void getNotOwnEventByIdAndInitiatorIdTest() {
        Long userId = 1L;
        Long eventId = 8L;

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventByIdAndInitiatorId(eventId, userId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(22)
    void getNonExistentEventByIdAndInitiatorIdTest() {
        Long userId = 1L;
        Long eventId = 999L;

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventByIdAndInitiatorId(eventId, userId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(23)
    void cascadeEventDeletingTest() {
        Long userId = 3L;
        Long eventId = 7L;

        userRepository.deleteById(3L);

        assertThrows(EntityNotFoundException.class, () -> eventService.getEventByIdAndInitiatorId(userId, eventId));
    }

}