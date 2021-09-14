package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll();
    CategoryDto getById(Long id);
    CategoryDto add(CategoryDto categoryDto);
    CategoryDto update(Long id, CategoryDto categoryDto);
    String delete(Long id);
}
