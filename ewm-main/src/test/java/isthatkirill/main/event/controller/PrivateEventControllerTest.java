package isthatkirill.main.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.dto.NewEventDto;
import isthatkirill.main.event.dto.UpdateEventDto;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.model.EventStateAction;
import isthatkirill.main.event.service.EventServiceImpl;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.request.dto.EventRequestStatusUpdateRequestDto;
import isthatkirill.main.request.dto.EventRequestStatusUpdateResultDto;
import isthatkirill.main.request.dto.ParticipationRequestDto;
import isthatkirill.main.request.model.RequestProcessedState;
import isthatkirill.main.request.service.RequestServiceImpl;
import isthatkirill.main.user.dto.UserShortDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static isthatkirill.main.util.Formats.DATE_PATTERN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PrivateEventController.class)
class PrivateEventControllerTest {

    @MockBean
    private RequestServiceImpl requestService;

    @MockBean
    private EventServiceImpl eventService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final Long userId = 1L;
    private final Long eventId = 1L;
    private NewEventDto newEventDto;
    private UpdateEventDto updateEventDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private final EventFullDto eventFullDto = EventFullDto.builder()
            .description("description_description_at_least_20_char")
            .annotation("annotation_annotation_at_least_20_char")
            .eventDate(LocalDateTime.now().plusHours(4))
            .category(CategoryDto.builder().id(1L).name("cat_name").build())
            .location(LocationDto.builder().lon(10.10f).lat(20.20f).build())
            .paid(false)
            .createdOn(LocalDateTime.now())
            .confirmedRequests(200L)
            .views(200L)
            .initiator(UserShortDto.builder().id(1L).name("user_name").build())
            .participantLimit(100)
            .requestModeration(false)
            .title("title_at_least_3_char")
            .state(EventState.PUBLISHED)
            .build();

    private final EventShortDto eventShortDto = EventShortDto.builder()
            .annotation("annotation_annotation_at_least_20_char")
            .eventDate(LocalDateTime.now().plusHours(4))
            .category(CategoryDto.builder().id(1L).name("cat_name").build())
            .paid(false)
            .confirmedRequests(200L)
            .views(200L)
            .initiator(UserShortDto.builder().id(1L).name("user_name").build())
            .title("title_at_least_3_char")
            .build();

    private final EventRequestStatusUpdateRequestDto updateRequest = EventRequestStatusUpdateRequestDto.builder()
            .requestIds(List.of(1L, 2L))
            .status(RequestProcessedState.CONFIRMED)
            .build();

    private final ParticipationRequestDto requestDtoOne = ParticipationRequestDto.builder()
            .id(1L)
            .requester(2L)
            .event(eventId)
            .created(LocalDateTime.now()).build();

    private final ParticipationRequestDto requestDtoTwo = ParticipationRequestDto.builder()
            .id(1L)
            .requester(2L)
            .event(eventId)
            .created(LocalDateTime.now()).build();

    private final EventRequestStatusUpdateResultDto resultRequestUpdatingDto = EventRequestStatusUpdateResultDto.builder()
            .confirmedRequests(List.of(requestDtoOne))
            .rejectedRequests(List.of(requestDtoTwo))
            .build();

    @Test
    @SneakyThrows
    void createTest() {
        rebuildNewEventDto();

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(eventFullDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$.category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$.location").value(eventFullDto.getLocation()))
                .andExpect(jsonPath("$.paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$.confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$.initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$.participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$.requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$.views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$.state").value(eventFullDto.getState().name()));

        verify(eventService, times(1)).create(newEventDto, userId);
    }

    @Test
    @SneakyThrows
    void createWithShortAnnotationTest() {
        rebuildNewEventDto();
        newEventDto.setAnnotation("annotation");

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createWithNullCategoryTest() {
        rebuildNewEventDto();
        newEventDto.setCategory(null);

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createWithShortDescriptionTest() {
        rebuildNewEventDto();
        newEventDto.setDescription("description");

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createWithTooEarlyEventDateTest() {
        rebuildNewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(1));

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createWithNegativeParticipantLimitTest() {
        rebuildNewEventDto();
        newEventDto.setParticipantLimit(-1);

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createWithShortTitleTest() {
        rebuildNewEventDto();
        newEventDto.setTitle("x");

        when(eventService.create(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).create(any(), anyLong());
    }


    @Test
    @SneakyThrows
    void getAllByInitiatorIdTest() {
        Integer defaultFrom = 0;
        Integer defaultSize = 10;

        when(eventService.getAllByInitiatorId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(eventShortDto));

        mvc.perform(get("/users/{userId}/events", userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].annotation").value(eventShortDto.getAnnotation()))
                .andExpect(jsonPath("$[0].eventDate").value(eventShortDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$[0].category.id").value(eventShortDto.getCategory().getId()))
                .andExpect(jsonPath("$[0].paid").value(eventShortDto.getPaid()))
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventShortDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].initiator").value(eventShortDto.getInitiator()))
                .andExpect(jsonPath("$[0].title").value(eventShortDto.getTitle()))
                .andExpect(jsonPath("$[0].views").value(eventShortDto.getViews()))
                .andExpect(jsonPath("$[0].views").value(eventShortDto.getViews()));

        verify(eventService, times(1)).getAllByInitiatorId(userId, defaultFrom, defaultSize);
    }

    @Test
    @SneakyThrows
    void getAllByInitiatorIdWithParamsTest() {
        Integer from = 2;
        Integer size = 2;

        when(eventService.getAllByInitiatorId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(eventShortDto));

        mvc.perform(get("/users/{userId}/events", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].annotation").value(eventShortDto.getAnnotation()))
                .andExpect(jsonPath("$[0].eventDate").value(eventShortDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$[0].category.id").value(eventShortDto.getCategory().getId()))
                .andExpect(jsonPath("$[0].paid").value(eventShortDto.getPaid()))
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventShortDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].initiator").value(eventShortDto.getInitiator()))
                .andExpect(jsonPath("$[0].title").value(eventShortDto.getTitle()))
                .andExpect(jsonPath("$[0].views").value(eventShortDto.getViews()))
                .andExpect(jsonPath("$[0].views").value(eventShortDto.getViews()));

        verify(eventService, times(1)).getAllByInitiatorId(userId, from, size);
    }

    @Test
    @SneakyThrows
    void getEventByIdAndInitiatorIdTest() {
        when(eventService.getEventByIdAndInitiatorId(anyLong(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(get("/users/{userId}/events/{eventId}", userId, eventId)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(eventFullDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$.category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$.location").value(eventFullDto.getLocation()))
                .andExpect(jsonPath("$.paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$.participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$.requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$.views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$.confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$.initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$.state").value(eventFullDto.getState().name()));

        verify(eventService, times(1)).getEventByIdAndInitiatorId(eventId, userId);
    }

    @Test
    @SneakyThrows
    void updateByInitiatorTest() {
        rebuildUpdateEventDto();
        when(eventService.updateByInitiator(any(), anyLong(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEventDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.eventDate").value(eventFullDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$.category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$.location").value(eventFullDto.getLocation()))
                .andExpect(jsonPath("$.paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$.confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$.initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$.participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$.requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$.views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$.state").value(eventFullDto.getState().name()));

        verify(eventService, times(1)).updateByInitiator(updateEventDto, eventId, userId);
    }

    @Test
    @SneakyThrows
    void updateByInitiatorWithTooShortAnnotationTest() {
        rebuildUpdateEventDto();
        updateEventDto.setAnnotation("annotation");
        when(eventService.updateByInitiator(any(), anyLong(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEventDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByInitiator(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByInitiatorWithTooShortDescriptionTest() {
        rebuildUpdateEventDto();
        updateEventDto.setDescription("description");
        when(eventService.updateByInitiator(any(), anyLong(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEventDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByInitiator(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByInitiatorWithEarlyEventDateTest() {
        rebuildUpdateEventDto();
        updateEventDto.setEventDate(LocalDateTime.now().plusHours(1));
        when(eventService.updateByInitiator(any(), anyLong(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEventDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByInitiator(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByInitiatorWithNegativeParticipantLimitTest() {
        rebuildUpdateEventDto();
        updateEventDto.setParticipantLimit(-1);
        when(eventService.updateByInitiator(any(), anyLong(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEventDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByInitiator(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByInitiatorWithTooShortTitleTest() {
        rebuildUpdateEventDto();
        updateEventDto.setTitle("x");
        when(eventService.updateByInitiator(any(), anyLong(), anyLong()))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEventDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByInitiator(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void processRequestsByInitiatorTest() {
        when(requestService.processRequestsByInitiator(any(), anyLong(), anyLong())).thenReturn(resultRequestUpdatingDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}/requests", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.confirmedRequests[0].id").value(resultRequestUpdatingDto.getConfirmedRequests().get(0).getId()))
                .andExpect(jsonPath("$.rejectedRequests[0].id").value(resultRequestUpdatingDto.getRejectedRequests().get(0).getId()));

        verify(requestService, times(1)).processRequestsByInitiator(updateRequest, userId, eventId);
    }

    @Test
    @SneakyThrows
    void processRequestsByInitiatorWithEmptyListTest() {
        updateRequest.setRequestIds(Collections.emptyList());

        when(requestService.processRequestsByInitiator(any(), anyLong(), anyLong())).thenReturn(resultRequestUpdatingDto);

        mvc.perform(patch("/users/{userId}/events/{eventId}/requests", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(requestService, never()).processRequestsByInitiator(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestsByInitiator() {
        when(requestService.getRequestsByInitiator(anyLong(), anyLong())).thenReturn(List.of(requestDtoOne, requestDtoTwo));

        mvc.perform(get("/users/{userId}/events/{eventId}/requests", userId, eventId)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(requestDtoOne.getId()))
                .andExpect(jsonPath("$[0].requester").value(requestDtoOne.getRequester()))
                .andExpect(jsonPath("$[1].id").value(requestDtoTwo.getId()))
                .andExpect(jsonPath("$[1].requester").value(requestDtoTwo.getRequester()));

        verify(requestService, times(1)).getRequestsByInitiator(userId, eventId);
    }

    private void rebuildNewEventDto() {
        newEventDto = NewEventDto.builder()
                .annotation("annotation_at_least_20_char")
                .description("description_at_least_20_char")
                .category(1L)
                .eventDate(LocalDateTime.parse("2023-12-12 20:57:50", formatter))
                .location(LocationDto.builder().lat(10.10f).lon(20.20f).build())
                .paid(false)
                .participantLimit(100)
                .requestModeration(false)
                .title("title_at_least_3_char")
                .build();
    }

    private void rebuildUpdateEventDto() {
        updateEventDto = UpdateEventDto.builder()
                .description("description_description_at_least_20_char")
                .annotation("annotation_annotation_at_least_20_char")
                .eventDate(LocalDateTime.now().plusHours(4))
                .category(1L)
                .location(LocationDto.builder().lon(10.10f).lat(20.20f).build())
                .paid(false)
                .participantLimit(100)
                .requestModeration(false)
                .title("title_at_least_3_char")
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .build();
    }

}