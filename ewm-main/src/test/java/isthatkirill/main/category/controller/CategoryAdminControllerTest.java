package isthatkirill.main.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.category.dto.NewCategoryDto;
import isthatkirill.main.category.service.CategoryServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryAdminController.class)
class CategoryAdminControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    private NewCategoryDto newCategoryDto;

    private final CategoryDto categoryDto = CategoryDto.builder()
            .id(1L)
            .name("name")
            .build();

    @BeforeEach
    void buildDto() {
        newCategoryDto = NewCategoryDto.builder()
                .name("name").build();
    }

    @Test
    @SneakyThrows
    void createTest() {
        when(categoryService.create(any())).thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).create(newCategoryDto);
    }

    @Test
    @SneakyThrows
    void createWithInvalidNameTest() {
        newCategoryDto.setName(null);
        when(categoryService.create(any())).thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(categoryService, never()).create(any());
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long catId = 1L;

        when(categoryService.update(any(), anyLong())).thenReturn(categoryDto);

        mvc.perform(patch("/admin/categories/{catId}", catId)
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).update(newCategoryDto, catId);
    }

    @Test
    @SneakyThrows
    void updateWithInvalidNameTest() {
        newCategoryDto.setName(null);
        Long catId = 1L;

        when(categoryService.update(any(), anyLong())).thenReturn(categoryDto);

        mvc.perform(patch("/admin/categories/{catId}", catId)
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(categoryService, never()).update(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        Long catId = 1L;

        mvc.perform(delete("/admin/categories/{catId}", catId)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(categoryService, times(1)).delete(catId);
    }
}