package io.nbc.selectedseat.domain.category.repository;

import io.nbc.selectedseat.domain.category.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(final String name);

    Optional<Category> findByName(final String name);

    Optional<Category> findById(final Long categoryId);

    Category update(final Category category);

    void delete(final Category category);

    List<Category> getCategories();
}
