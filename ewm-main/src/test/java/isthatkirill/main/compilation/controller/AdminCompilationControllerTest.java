package isthatkirill.main.compilation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.compilation.dto.CompilationRequestDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;
import isthatkirill.main.compilation.service.CompilationServiceImpl;
import isthatkirill.main.event.dto.EventShortDto;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Kirill Emelyanov
 */

@WebMvcTest(controllers = AdminCompilationController.class)
class AdminCompilationControllerTest {

    @MockBean
    private CompilationServiceImpl compilationService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private CompilationRequestDto compilationRequestDto;

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

    @BeforeEach
    void rebuildCompilationRequest() {
        compilationRequestDto = CompilationRequestDto.builder()
                .events(List.of(1L))
                .title("title")
                .pinned(true)
                .build();
    }

    @Test
    @SneakyThrows
    void createTest() {
        when(compilationService.create(any())).thenReturn(compilationResponseDto);

        mvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compilationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
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

        verify(compilationService, times(1)).create(compilationRequestDto);
    }

    @Test
    @SneakyThrows
    void createWithBlankTitleTest() {
        compilationRequestDto.setTitle("    ");
        when(compilationService.create(any())).thenReturn(compilationResponseDto);

        mvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compilationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(compilationService, never()).create(any());
    }

    @Test
    @SneakyThrows
    void createWithTooLongTitleTest() {
        compilationRequestDto.setTitle("too_long_title_############################################################");
        when(compilationService.create(any())).thenReturn(compilationResponseDto);

        mvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compilationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(compilationService, never()).create(any());
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long compId = 1L;

        when(compilationService.update(any(), anyLong())).thenReturn(compilationResponseDto);

        mvc.perform(patch("/admin/compilations/{compId}", compId).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compilationRequestDto))
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

        verify(compilationService, times(1)).update(compilationRequestDto, compId);
    }

    @Test
    @SneakyThrows
    void updateWithEmptyTitleShouldBeOkTest() {
        compilationRequestDto.setTitle(null);
        compilationRequestDto.setPinned(null);
        Long compId = 1L;

        when(compilationService.update(any(), anyLong())).thenReturn(compilationResponseDto);

        mvc.perform(patch("/admin/compilations/{compId}", compId).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compilationRequestDto))
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

        verify(compilationService, times(1)).update(compilationRequestDto, compId);
    }

    @Test
    @SneakyThrows
    void updateWithTooLongTitleTest() {
        compilationRequestDto.setTitle("too_long_title_############################################################");
        Long compId = 1L;

        when(compilationService.update(any(), anyLong())).thenReturn(compilationResponseDto);

        mvc.perform(patch("/admin/compilations/{compId}", compId).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(compilationRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(compilationService, never()).update(any(), anyLong());
    }


    @Test
    @SneakyThrows
    void deleteTest() {
        Long compId = 1L;

        mvc.perform(delete("/admin/compilations/{compId}", compId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(compilationService, times(1)).delete(compId);
    }
}