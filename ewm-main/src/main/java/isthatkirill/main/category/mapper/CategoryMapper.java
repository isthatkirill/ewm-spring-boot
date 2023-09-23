package isthatkirill.main.category.mapper;

import isthatkirill.main.category.dto.CategoryDto;
import isthatkirill.main.category.dto.NewCategoryDto;
import isthatkirill.main.category.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(NewCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDto(List<Category> categories);

}
