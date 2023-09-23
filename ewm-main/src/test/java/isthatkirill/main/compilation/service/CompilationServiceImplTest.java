package isthatkirill.main.compilation.service;

import isthatkirill.main.compilation.dto.CompilationRequestDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;
import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.event.dto.EventShortDto;
import isthatkirill.main.event.service.StatServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Kirill Emelyanov
 */

@SpringBootTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompilationServiceImplTest {

    @Autowired
    private CompilationServiceImpl compilationService;

    @MockBean
    private StatServiceImpl statService;

    //currentCompId = 7 --> test-compilations.sql contains previous 6 events

    @BeforeAll
    void configureMock() {
        when(statService.getViews(anyList())).thenReturn(Collections.emptyMap());
        when(statService.getConfirmedRequests(anyList())).thenReturn(Collections.emptyMap());
    }

    @Test
    @Order(1)
    @Sql(value = {"/testdata/drop-table.sql", "/schema.sql", "/testdata/test-events.sql", "/testdata/test-compilations.sql"})
    void createTest() {
        CompilationRequestDto compilationRequestDto = CompilationRequestDto.builder()
                .events(List.of(1L, 2L))
                .title("c-style compilation")
                .pinned(true)
                .build();

        CompilationResponseDto compilationResponseDto = compilationService.create(compilationRequestDto);

        assertThat(compilationResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", 8L)
                .hasFieldOrPropertyWithValue("title", compilationRequestDto.getTitle())
                .hasFieldOrPropertyWithValue("pinned", compilationRequestDto.getPinned());

        List<Long> eventsId = compilationResponseDto.getEvents().stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        assertThat(eventsId).hasSize(2)
                .containsExactlyInAnyOrder(1L, 2L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(2)
    void createWithNonexistentTest() {
        CompilationRequestDto compilationRequestDto = CompilationRequestDto.builder()
                .events(List.of(1L, 999L))
                .title("create compilation with non existent event")
                .pinned(true)
                .build();

        assertThrows(EntityNotFoundException.class, () -> compilationService.create(compilationRequestDto));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(3)
    void createEmptyCompilationWithNullPinnedFieldTest() {
        CompilationRequestDto compilationRequestDto = CompilationRequestDto.builder()
                .events(Collections.emptyList())
                .title("empty compilation with null pinned field")
                .build();

        CompilationResponseDto compilationResponseDto = compilationService.create(compilationRequestDto);

        assertThat(compilationResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", 9L)
                .hasFieldOrPropertyWithValue("title", compilationRequestDto.getTitle())
                .hasFieldOrPropertyWithValue("pinned", false);

        List<Long> eventsId = compilationResponseDto.getEvents().stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        assertThat(eventsId).isEmpty();

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(4)
    void updateTest() {
        Long compId = 2L;

        CompilationRequestDto compilationRequestDto = CompilationRequestDto.builder()
                .events(List.of(3L))
                .title("only NASM compilation")
                .pinned(false)
                .build();

        CompilationResponseDto compilationResponseDto = compilationService.update(compilationRequestDto, compId);

        assertThat(compilationResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", compId)
                .hasFieldOrPropertyWithValue("title", compilationRequestDto.getTitle())
                .hasFieldOrPropertyWithValue("pinned", compilationRequestDto.getPinned());

        List<Long> eventsId = compilationResponseDto.getEvents().stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        assertThat(eventsId).hasSize(1)
                .containsExactlyInAnyOrder(3L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(5)
    void updateWithNotExistentEventsTest() {
        Long compId = 2L;

        CompilationRequestDto compilationRequestDto = CompilationRequestDto.builder()
                .events(List.of(3L, 100L))
                .title("update compilation with non existent event")
                .pinned(false)
                .build();

        assertThrows(EntityNotFoundException.class, () -> compilationService.update(compilationRequestDto, compId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(6)
    void deleteTest() {
        Long compId = 9L;

        assertDoesNotThrow(() -> compilationService.delete(compId));
        assertThrows(EntityNotFoundException.class, () -> compilationService.delete(compId));
    }

    @Test
    @Order(7)
    void deleteNonExistentCompilationTest() {
        Long compId = 999L;

        assertThrows(EntityNotFoundException.class, () -> compilationService.delete(compId));
    }

    @Test
    @Order(8)
    void getByIdTest() {
        Long compId = 3L;

        CompilationResponseDto compilationResponseDto = compilationService.getById(compId);

        assertThat(compilationResponseDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", compId)
                .hasFieldOrPropertyWithValue("title", "spring compilation")
                .hasFieldOrPropertyWithValue("pinned", true);

        List<Long> eventsId = compilationResponseDto.getEvents().stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        assertThat(eventsId).hasSize(4)
                .containsExactlyInAnyOrder(9L, 10L, 11L, 12L);

        verify(statService, times(1)).getViews(anyList());
        verify(statService, times(1)).getConfirmedRequests(anyList());
    }

    @Test
    @Order(8)
    void getNonExistentCompilationByIdTest() {
        Long compId = 999L;

        assertThrows(EntityNotFoundException.class, () -> compilationService.getById(compId));

        verify(statService, never()).getViews(anyList());
        verify(statService, never()).getConfirmedRequests(anyList());
    }

    @Test
    @Order(9)
    void getAllTest() {
        Boolean pinned = false;
        Integer defaultFrom = 0;
        Integer defaultSize = 10;

        List<CompilationResponseDto> compilations = compilationService.getAll(pinned, defaultFrom, defaultSize);

        assertThat(compilations).hasSize(5)
                .extracting(CompilationResponseDto::getId)
                .containsExactlyInAnyOrder(2L, 4L, 5L, 6L, 7L);
    }

    @Test
    @Order(10)
    void getAllWithPaginationTest() {
        Boolean pinned = true;
        Integer from = 0;
        Integer size = 2;

        List<CompilationResponseDto> compilations = compilationService.getAll(pinned, from, size);

        assertThat(compilations).hasSize(2)
                .extracting(CompilationResponseDto::getId)
                .containsExactlyInAnyOrder(1L, 3L);
    }

}