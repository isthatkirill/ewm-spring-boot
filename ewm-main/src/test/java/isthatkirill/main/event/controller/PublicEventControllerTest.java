package isthatkirill.main.event.controller;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.event.dto.EventFullDto;
import isthatkirill.main.event.model.EventSort;
import isthatkirill.main.event.model.EventState;
import isthatkirill.main.event.service.EventServiceImpl;
import isthatkirill.main.location.dto.LocationDto;
import isthatkirill.main.user.dto.UserShortDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static isthatkirill.main.util.Formats.DATE_PATTERN;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PublicEventController.class)
class PublicEventControllerTest {

    @MockBean
    private EventServiceImpl eventService;

    @Autowired
    private MockMvc mvc;

    private final EventFullDto eventFullDto = EventFullDto.builder()
            .description("description_description_at_least_20_char")
            .annotation("annotation_annotation_at_least_20_char")
            .eventDate(LocalDateTime.now().plusHours(4))
            .category(CategoryDto.builder().id(1L).name("cat_name").build())
            .location(LocationDto.builder().lon(10.10f).lat(20.20f).build())
            .paid(false)
            .createdOn(LocalDateTime.now())
            .confirmedRequests(200L)
            .initiator(UserShortDto.builder().id(1L).name("user_name").build())
            .participantLimit(100)
            .views(200L)
            .requestModeration(false)
            .title("title_at_least_3_char")
            .state(EventState.PUBLISHED)
            .build();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final Long eventId = 1L;


    @Test
    @SneakyThrows
    void getEventByPublicTest() {
        String remoteAddr = "127.0.0.1";
        String requestURI = "/events/" + eventId;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        request.setRequestURI(requestURI);

        when(eventService.getEventByPublic(anyLong(), anyString(), anyString()))
                .thenReturn(eventFullDto);

        mvc.perform(get("/events/{id}", eventId)
                        .requestAttr("javax.servlet.http.HttpServletRequest", request)
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
                .andExpect(jsonPath("$.participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$.requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$.confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$.initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$.views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$.state").value(eventFullDto.getState().name()));

        verify(eventService, times(1)).getEventByPublic(eventId, requestURI, remoteAddr);
    }

    @Test
    @SneakyThrows
    void getAllEventsByPublicTest() {
        String text = "text";
        List<Long> categories = List.of(1L, 2L);
        Boolean paid = true;
        Boolean onlyAvailable = true;
        String start = LocalDateTime.now().minusDays(4).format(formatter);
        String end = LocalDateTime.now().plusDays(5).format(formatter);
        EventSort sort = EventSort.EVENT_DATE;
        Integer from = 0;
        Integer size = 10;
        String remoteAddr = "127.0.0.1";
        String requestURI = "/events";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        request.setRequestURI(requestURI);

        when(eventService.getAllEventsByPublic(anyString(), anyList(), anyBoolean(),
                any(), any(), anyBoolean(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(List.of(eventFullDto));

        mvc.perform(get("/events")
                        .param("text", text)
                        .param("categories", String.valueOf(categories.get(0)), String.valueOf(categories.get(1)))
                        .param("paid", String.valueOf(paid))
                        .param("rangeStart", start)
                        .param("rangeEnd", end)
                        .param("onlyAvailable", String.valueOf(onlyAvailable))
                        .param("sort", sort.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .requestAttr("javax.servlet.http.HttpServletRequest", request)
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
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$[0].initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$[0].views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$[0].state").value(eventFullDto.getState().name()));

        verify(eventService, times(1)).getAllEventsByPublic(text, categories, paid,
                LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter),
                onlyAvailable, sort, from, size, requestURI, remoteAddr);
    }

    @Test
    @SneakyThrows
    void getAllEventsByPublicWithoutParamsTest() {
        String remoteAddr = "127.0.0.1";
        String requestURI = "/events";
        Integer defaultFrom = 0;
        Integer defaultSize = 10;
        Boolean defaultAvailable = false;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        request.setRequestURI(requestURI);

        when(eventService.getAllEventsByPublic(any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(eventFullDto));

        mvc.perform(get("/events")
                        .requestAttr("javax.servlet.http.HttpServletRequest", request)
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
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].createdOn").value(eventFullDto.getCreatedOn().format(formatter)))
                .andExpect(jsonPath("$[0].initiator").value(eventFullDto.getInitiator()))
                .andExpect(jsonPath("$[0].views").value(eventFullDto.getViews()))
                .andExpect(jsonPath("$[0].state").value(eventFullDto.getState().name()));

        verify(eventService, times(1)).getAllEventsByPublic(null, null, null,
                null, null, defaultAvailable, null, defaultFrom, defaultSize, requestURI, remoteAddr);
    }

    @Test
    @SneakyThrows
    void getAllEventsByPublicWithInvalidParamsTest() {
        Integer from = -100;
        Integer size = 10;

        when(eventService.getAllEventsByPublic(any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(eventFullDto));

        mvc.perform(get("/events")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(eventService, never()).getAllEventsByPublic(any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any());
    }

}