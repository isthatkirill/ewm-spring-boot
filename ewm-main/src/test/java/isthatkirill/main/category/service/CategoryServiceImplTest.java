package isthatkirill.main.category.service;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.category.dto.NewCategoryDto;
import isthatkirill.main.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    @Order(1)
    @Sql(value = {"/testdata/drop-table.sql", "/schema.sql"})
    void createTest() {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder()
                .name("name")
                .build();

        CategoryDto categoryDto = categoryService.create(newCategoryDto);

        assertThat(categoryDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", newCategoryDto.getName());
    }

    @Test
    @Order(2)
    void createWithSameNameTest() {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder()
                .name("name")
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> categoryService.create(newCategoryDto));
    }

    @Test
    @Order(3)
    void updateTest() {
        Long catId = 1L;
        NewCategoryDto newCategoryDto = NewCategoryDto.builder()
                .name("name [updated]")
                .build();

        CategoryDto categoryDto = categoryService.update(newCategoryDto, catId);

        assertThat(categoryDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", newCategoryDto.getName());
    }

    @Test
    @Order(4)
    void updateNonExistentTest() {
        Long catId = Long.MAX_VALUE;
        NewCategoryDto newCategoryDto = NewCategoryDto.builder()
                .name("name [updated]")
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(newCategoryDto, catId));
    }

    @Test
    @Order(5)
    void createAndUpdateWithNotUniqueNameTest() {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder()
                .name("name")
                .build();

        CategoryDto categoryDto = categoryService.create(newCategoryDto);

        NewCategoryDto newCategoryDtoUpdated = NewCategoryDto.builder()
                .name("name [updated]")
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> categoryService.update(newCategoryDtoUpdated, categoryDto.getId()));
    }

    @Test
    @Order(6)
    @Sql(value = "/testdata/test-categories.sql")
    void getByIdTest() {
        Long catId = 4L;

        CategoryDto categoryDto = categoryService.getById(catId);

        assertThat(categoryDto).isNotNull()
                .hasFieldOrPropertyWithValue("name", "name1");
    }

    @Test
    @Order(6)
    void getByIdNonExistentTest() {
        Long catId = Long.MAX_VALUE;

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(catId));
    }

    @Test
    @Order(7)
    void getAllTest() {
        Integer from = 3;
        Integer size = 3;

        List<CategoryDto> categoryDtos = categoryService.getAll(from, size);

        assertThat(categoryDtos).hasSize(3)
                .extracting(CategoryDto::getName)
                .containsExactly("name2", "name3", "name4");
    }

    @Test
    @Order(8)
    void deleteTest() {
        assertDoesNotThrow(() -> categoryService.delete(3L));
        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(3L));
    }

}