package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.service.CategoryService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory (@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(categoryService.add(categoryDto));
    }
    @GetMapping
    public ResponseEntity<?> getCategories(){
        return ResponseEntity.ok().body(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id){
        return  ResponseEntity.ok().body(categoryService.getById(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        return ResponseEntity.ok().body(categoryService.update(id, categoryDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id){
        return ResponseEntity.ok().body(categoryService.delete(id));
    }
}
