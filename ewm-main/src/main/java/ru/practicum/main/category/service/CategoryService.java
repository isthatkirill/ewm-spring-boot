package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(NewCategoryDto newCategoryDto, Long catId);

    void delete(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Integer from, Integer size);

}
