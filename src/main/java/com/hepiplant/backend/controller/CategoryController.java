package com.hepiplant.backend.controller;

import com.hepiplant.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> getCategories(){
        return ResponseEntity.ok().body(categoryService.getAll());
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id){
        return  ResponseEntity.ok().body(categoryService.getById(id));
    }
}
