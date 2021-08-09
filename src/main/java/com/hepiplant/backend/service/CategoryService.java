package com.hepiplant.backend.service;

import com.hepiplant.backend.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<Category> getAll();
    Category getById(Long id);
}
