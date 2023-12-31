package isthatkirill.main.category.service;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.category.dto.NewCategoryDto;
import isthatkirill.main.category.mapper.CategoryMapper;
import isthatkirill.main.category.model.Category;
import isthatkirill.main.category.repository.CategoryRepository;
import isthatkirill.main.error.exception.EntityNotFoundException;
import isthatkirill.main.error.exception.ForbiddenException;
import isthatkirill.main.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("New category added --> id={}", category.getId());
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto update(NewCategoryDto newCategoryDto, Long catId) {
        checkIfCategoryExists(catId);
        Category updatedCategory = categoryMapper.toCategory(newCategoryDto);
        updatedCategory.setId(catId);
        log.info("Category updated --> {}", updatedCategory.getId());
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

    private void checkIfEmpty(Long catId) {
        if (!eventRepository.findEventsByCategoryId(catId).isEmpty()) {
            throw new ForbiddenException("The category is not empty");
        }
    }

    private Category checkIfCategoryExistsAndGet(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, catId));
    }

    private void checkIfCategoryExists(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException(Category.class, catId);
        }
    }

}
