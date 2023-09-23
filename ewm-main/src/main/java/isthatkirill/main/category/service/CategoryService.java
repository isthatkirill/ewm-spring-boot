package isthatkirill.main.category.service;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(NewCategoryDto newCategoryDto, Long catId);

    void delete(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Integer from, Integer size);

}
