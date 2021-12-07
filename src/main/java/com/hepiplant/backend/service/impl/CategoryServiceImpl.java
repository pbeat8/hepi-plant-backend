package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.service.CategoryService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;
import static java.util.Optional.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BeanValidator beanValidator;

    public CategoryServiceImpl(final CategoryRepository categoryRepository, final BeanValidator beanValidator) {
        this.categoryRepository = categoryRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Category not found for id "+id));
        return mapToDto(category);
    }

    @Override
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        beanValidator.validate(category);
        Category savedCategories = categoryRepository.save(category);
        return mapToDto(savedCategories);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found for id "+id));
        ofNullable(categoryDto.getName())
                .ifPresent(c -> category.setName(categoryDto.getName()));
        beanValidator.validate(category);
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }

    @Override
    public String delete(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty())
        {
            return "No category with id = " + id;
        }
        Category categoryValue = category.get();
        categoryValue.getPlantList()
                .forEach(p -> p.setCategory(null));
        categoryValue.getSpeciesList()
                .forEach(p -> p.setCategory(null));
        categoryValue.getPostList()
                .forEach(p -> p.setCategory(null));
        categoryValue.getSalesOfferList()
                .forEach(p -> p.setCategory(null));
        categoryRepository.delete(categoryValue);
        return "Successfully deleted the category with id = "+id;
    }
}
