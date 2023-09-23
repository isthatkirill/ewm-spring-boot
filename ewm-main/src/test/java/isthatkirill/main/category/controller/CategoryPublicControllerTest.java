package isthatkirill.main.category.controller;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.category.service.CategoryServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryPublicController.class)
class CategoryPublicControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    private final CategoryDto categoryDto = CategoryDto.builder()
            .id(1L)
            .name("name")
            .build();

    @Test
    @SneakyThrows
    void getByIdTest() {
        Long catId = 1L;

        when(categoryService.getById(anyLong())).thenReturn(categoryDto);

        mvc.perform(get("/categories/{catId}", catId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).getById(catId);
    }

    @Test
    @SneakyThrows
    void getCategoriesWithDefaultParamsTest() {
        Integer defaultFrom = 0;
        Integer defaultSize = 10;

        when(categoryService.getAll(anyInt(), anyInt())).thenReturn(List.of(categoryDto));

        mvc.perform(get("/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(categoryDto.getId()))
                .andExpect(jsonPath("$[0].name").value(categoryDto.getName()));

        verify(categoryService, times(1)).getAll(defaultFrom, defaultSize);
    }

    @Test
    @SneakyThrows
    void getCategoriesWithParamsTest() {
        Integer from = 2;
        Integer size = 4;

        when(categoryService.getAll(anyInt(), anyInt())).thenReturn(List.of(categoryDto));

        mvc.perform(get("/categories")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(categoryDto.getId()))
                .andExpect(jsonPath("$[0].name").value(categoryDto.getName()));

        verify(categoryService, times(1)).getAll(from, size);
    }

    @Test
    @SneakyThrows
    void getCategoriesWithInvalidParamsTest() {
        Integer from = Integer.MIN_VALUE;
        Integer size = 4;

        when(categoryService.getAll(anyInt(), anyInt())).thenReturn(List.of(categoryDto));

        mvc.perform(get("/categories")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(categoryService, never()).getAll(anyInt(), anyInt());
    }

}