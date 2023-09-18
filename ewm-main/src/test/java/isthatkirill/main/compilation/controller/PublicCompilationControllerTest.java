package isthatkirill.main.compilation.controller;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;
import isthatkirill.main.compilation.service.CompilationServiceImpl;
import isthatkirill.main.event.dto.EventShortDto;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Kirill Emelyanov
 */

@WebMvcTest(controllers = PublicCompilationController.class)
class PublicCompilationControllerTest {

    @MockBean
    private CompilationServiceImpl compilationService;

    @Autowired
    private MockMvc mvc;

    private final EventShortDto eventShortDto = EventShortDto.builder()
            .annotation("annotation")
            .eventDate(LocalDateTime.now().plusHours(4))
            .category(CategoryDto.builder().id(1L).name("cat_name").build())
            .paid(false)
            .confirmedRequests(200L)
            .views(200L)
            .initiator(UserShortDto.builder().id(1L).name("user_name").build())
            .title("title")
            .build();

    private final CompilationResponseDto compilationResponseDto = CompilationResponseDto.builder()
            .id(1L)
            .events(List.of(eventShortDto))
            .pinned(true)
            .title("title")
            .build();

    @Test
    @SneakyThrows
    void getByIdTest() {
        Long compId = 1L;

        when(compilationService.getById(any())).thenReturn(compilationResponseDto);

        mvc.perform(get("/compilations/{compId}", compId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(compilationResponseDto.getId()))
                .andExpect(jsonPath("$.pinned").value(compilationResponseDto.getPinned()))
                .andExpect(jsonPath("$.title").value(compilationResponseDto.getTitle()))
                .andExpect(jsonPath("$.events[0].annotation").value(eventShortDto.getAnnotation()))
                .andExpect(jsonPath("$.events[0].category.name").value(eventShortDto.getCategory().getName()))
                .andExpect(jsonPath("$.events[0].paid").value(eventShortDto.getPaid()))
                .andExpect(jsonPath("$.events[0].confirmedRequests").value(eventShortDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.events[0].title").value(eventShortDto.getTitle()))
                .andExpect(jsonPath("$.events[0].initiator.name").value(eventShortDto.getInitiator().getName()))
                .andExpect(jsonPath("$.events[0].views").value(eventShortDto.getViews()));

        verify(compilationService, times(1)).getById(compId);
    }

    @Test
    @SneakyThrows
    void getAllTest() {
        boolean pinned = true;
        Integer from = 0;
        Integer size = 10;

        when(compilationService.getAll(anyBoolean(), anyInt(), anyInt())).thenReturn(List.of(compilationResponseDto));

        mvc.perform(get("/compilations")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("pinned", String.valueOf(pinned))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(compilationResponseDto.getId()))
                .andExpect(jsonPath("$[0].pinned").value(compilationResponseDto.getPinned()))
                .andExpect(jsonPath("$[0].title").value(compilationResponseDto.getTitle()))
                .andExpect(jsonPath("$[0].events[0].annotation").value(eventShortDto.getAnnotation()))
                .andExpect(jsonPath("$[0].events[0].category.name").value(eventShortDto.getCategory().getName()))
                .andExpect(jsonPath("$[0].events[0].paid").value(eventShortDto.getPaid()))
                .andExpect(jsonPath("$[0].events[0].confirmedRequests").value(eventShortDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].events[0].title").value(eventShortDto.getTitle()))
                .andExpect(jsonPath("$[0].events[0].initiator.name").value(eventShortDto.getInitiator().getName()))
                .andExpect(jsonPath("$[0].events[0].views").value(eventShortDto.getViews()));

        verify(compilationService, times(1)).getAll(pinned, from, size);
    }


    @Test
    @SneakyThrows
    void getAllWithInvalidFromParamTest() {
        boolean pinned = true;
        Integer from = -1;
        Integer size = 10;

        when(compilationService.getAll(anyBoolean(), anyInt(), anyInt())).thenReturn(List.of(compilationResponseDto));

        mvc.perform(get("/compilations")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("pinned", String.valueOf(pinned))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(compilationService, never()).getAll(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllWithInvalidSizeParamTest() {
        boolean pinned = true;
        Integer from = 2;
        Integer size = 0;

        when(compilationService.getAll(anyBoolean(), anyInt(), anyInt())).thenReturn(List.of(compilationResponseDto));

        mvc.perform(get("/compilations")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("pinned", String.valueOf(pinned))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(compilationService, never()).getAll(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllWithDefaultParamsParamTest() {
        boolean pinned = true;
        Integer defaultFrom = 0;
        Integer defaultSize = 10;

        when(compilationService.getAll(anyBoolean(), anyInt(), anyInt())).thenReturn(List.of(compilationResponseDto));

        mvc.perform(get("/compilations")
                        .param("pinned", String.valueOf(pinned))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(compilationService, times(1)).getAll(pinned, defaultFrom, defaultSize);
    }

}