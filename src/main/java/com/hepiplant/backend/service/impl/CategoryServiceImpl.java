package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.service.CategoryService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;
    private final BeanValidator beanValidator;

    public CategoryServiceImpl(CategoryRepository categoryRepository, BeanValidator beanValidator) {
        this.categoryRepository = categoryRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
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

        if(categoryDto.getName()!=null && !categoryDto.getName().isEmpty())
            category.setName(categoryDto.getName());
        beanValidator.validate(category);
        Category savedCategories = categoryRepository.save(category);
        return mapToDto(savedCategories);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found for id "+id));
        if(categoryDto.getName()!=null && !categoryDto.getName().isEmpty())
            category.setName(categoryDto.getName());
        beanValidator.validate(category);
        Category savedCategories = categoryRepository.save(category);
        return mapToDto(savedCategories);
    }

    @Override
    public String delete(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty())
        {
            return "No category with id = "+id;
        }
        categoryRepository.delete(category.get());
        return "Successfully deleted the category with id = "+id;
    }
    private CategoryDto mapToDto(Category category)
    {
        CategoryDto dto = new CategoryDto();
        if(category.getName()!=null)
            dto.setName(category.getName());
        return dto;
    }
}
