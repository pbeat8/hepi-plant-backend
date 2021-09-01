package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> getAll() {
        List<CategoryDto> categoryList = categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
        return categoryList;
    }

    @Override
    public CategoryDto getById(Long id) {
        return mapToDto(categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException()));
    }

    @Override
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = new Category();

        if(categoryDto.getName()!=null && !categoryDto.getName().isEmpty())
            category.setName(categoryDto.getName());
        Category savedCategories = categoryRepository.save(category);
        return mapToDto(savedCategories);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(categoryDto.getName()!=null && !categoryDto.getName().isEmpty())
            category.setName(categoryDto.getName());
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
