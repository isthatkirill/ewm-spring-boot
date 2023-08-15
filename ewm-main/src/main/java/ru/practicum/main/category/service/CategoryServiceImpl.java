package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.error.exception.ForbiddenException;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.service.EventService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));
        log.info("New category added --> {}", category);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(NewCategoryDto newCategoryDto, Long catId) {
        checkIfCategoryExistsAndGet(catId);
        Category updatedCategory = categoryMapper.toCategory(newCategoryDto);
        updatedCategory.setId(catId);
        log.info("Category updated --> {}", updatedCategory);
        return categoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        checkIfCategoryExistsAndGet(catId);
        checkIfEmpty(catId);
        categoryRepository.deleteById(catId);
        log.info("Category with id={} has been deleted", catId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        log.info("Get category with id={}", catId);
        return categoryMapper.toCategoryDto(checkIfCategoryExistsAndGet(catId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Get all categories from={}, size={}", from, size);
        return categoryMapper.toCategoryDto(categoryRepository.findAll(pageable).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long catId) {
        return checkIfCategoryExistsAndGet(catId);
    }

    private void checkIfEmpty(Long catId) {
        if (!eventRepository.findEventsByCategoryId(catId).isEmpty()) throw new ForbiddenException("The category is not empty");
    }


    private Category checkIfCategoryExistsAndGet(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, catId));
    }


}
