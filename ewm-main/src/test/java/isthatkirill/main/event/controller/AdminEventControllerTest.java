package isthatkirill.main.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.dto.UpdateEventDto;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.model.EventStateAction;
import isthatkirill.main.event.service.EventServiceImpl;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.user.dto.UserShortDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static isthatkirill.main.util.Formats.DATE_PATTERN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminEventController.class)
class AdminEventControllerTest {

    @MockBean
    private EventServiceImpl eventService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UpdateEventDto updateEventDto;
    private EventFullDto eventFullDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final Long eventId = 1L;

    @BeforeEach
    void buildUpdateEventDto() {
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

        eventFullDto = EventFullDto.builder()
                .description(updateEventDto.getDescription())
                .annotation(updateEventDto.getAnnotation())
                .eventDate(updateEventDto.getEventDate())
                .category(CategoryDto.builder().id(updateEventDto.getCategory()).name("cat_name").build())
                .location(updateEventDto.getLocation())
                .paid(updateEventDto.getPaid())
                .createdOn(LocalDateTime.now())
                .confirmedRequests(200L)
                .views(200L)
                .initiator(UserShortDto.builder().id(1L).name("user_name").build())
                .participantLimit(updateEventDto.getParticipantLimit())
                .requestModeration(updateEventDto.getRequestModeration())
                .title(updateEventDto.getTitle())
                .state(EventState.PUBLISHED)
                .build();
    }

    @Test
    @SneakyThrows
    void updateByAdminTest() {
        when(eventService.updateByAdmin(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", eventId)
                        .content(objectMapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
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

        verify(eventService, times(1)).updateByAdmin(updateEventDto, eventId);
    }

    @Test
    @SneakyThrows
    void updateByAdminWithTooShortAnnotationTest() {
        updateEventDto.setAnnotation("annotation");
        when(eventService.updateByAdmin(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", eventId)
                        .content(objectMapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByAdmin(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByAdminWithTooShortDescriptionTest() {
        updateEventDto.setDescription("description");
        when(eventService.updateByAdmin(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", eventId)
                        .content(objectMapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByAdmin(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByAdminWithTooEarlyEventDateTest() {
        updateEventDto.setEventDate(LocalDateTime.now().plusHours(1));
        when(eventService.updateByAdmin(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", eventId)
                        .content(objectMapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByAdmin(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByAdminWithNegativeParticipantLimitTest() {
        updateEventDto.setParticipantLimit(-1);
        when(eventService.updateByAdmin(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", eventId)
                        .content(objectMapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByAdmin(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateByAdminWithTooShortTitleTest() {
        updateEventDto.setTitle("t");
        when(eventService.updateByAdmin(any(), anyLong())).thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/{eventId}", eventId)
                        .content(objectMapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).updateByAdmin(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllEventsByAdminTest() {
        List<Long> users = List.of(1L, 2L);
        List<EventState> states = List.of(EventState.PUBLISHED);
        List<Long> categories = List.of(1L);
        String start = LocalDateTime.now().minusDays(4).format(formatter);
        String end = LocalDateTime.now().plusDays(5).format(formatter);
        Integer from = 2;
        Integer size = 3;

        when(eventService.getAllEventsByAdmin(
                anyList(), anyList(), anyList(),
                any(), any(), anyInt(), anyInt()
        )).thenReturn(List.of(eventFullDto));

        mvc.perform(get("/admin/events")
                        .param("users", String.valueOf(users.get(0)), String.valueOf(users.get(1)))
                        .param("states", String.valueOf(states.get(0)))
                        .param("categories", String.valueOf(categories.get(0)))
                        .param("rangeStart", start)
                        .param("rangeEnd", end)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$[0].annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$[0].eventDate").value(eventFullDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$[0].category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$[0].location").value(eventFullDto.getLocation()))
                .andExpect(jsonPath("$[0].paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$[0].participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$[0].requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$[0].title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$[0].views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$[0].initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$[0].state").value(eventFullDto.getState().name()));

        verify(eventService, times(1))
                .getAllEventsByAdmin(users, states, categories,
                        LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter), from, size);
    }

    @Test
    @SneakyThrows
    void getAllEventsByAdminWithoutParamsTest() {
        Integer defaultFrom = 0;
        Integer defaultSize = 10;

        when(eventService.getAllEventsByAdmin(
                any(), any(), any(),
                any(), any(), anyInt(), anyInt()
        )).thenReturn(List.of(eventFullDto));

        mvc.perform(get("/admin/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$[0].annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$[0].eventDate").value(eventFullDto.getEventDate().format(formatter)))
                .andExpect(jsonPath("$[0].category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$[0].location").value(eventFullDto.getLocation()))
                .andExpect(jsonPath("$[0].paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$[0].participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$[0].requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$[0].title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$[0].views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$[0].initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$[0].state").value(eventFullDto.getState().name()));

        verify(eventService, times(1))
                .getAllEventsByAdmin(null, null, null, null, null, defaultFrom, defaultSize);
    }

    @Test
    @SneakyThrows
    void getAllEventsByAdminWithBadPramsTest() {
        Integer from = -1000;
        Integer size = 10;

        when(eventService.getAllEventsByAdmin(
                any(), any(), any(),
                any(), any(), anyInt(), anyInt()
        )).thenReturn(List.of(eventFullDto));

        mvc.perform(get("/admin/events")
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never())
                .getAllEventsByAdmin(any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

}