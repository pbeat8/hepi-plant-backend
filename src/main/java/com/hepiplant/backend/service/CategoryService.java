package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    Category getById(Long id);
    CategoryDto add(CategoryDto categoryDto);
    CategoryDto update(Long id, CategoryDto categoryDto);
    String delete(Long id);
}
