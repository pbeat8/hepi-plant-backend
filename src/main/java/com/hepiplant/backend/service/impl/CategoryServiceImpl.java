package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.*;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.service.*;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final PostService postService;
    private final BeanValidator beanValidator;
    private final PlantService plantService;
    private final SalesOfferService salesOfferService;
    private final SpeciesService speciesService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, PostService postService, BeanValidator beanValidator, PlantService plantService, SalesOfferService salesOfferService, SpeciesService speciesService) {
        this.categoryRepository = categoryRepository;
        this.postService = postService;
        this.beanValidator = beanValidator;
        this.plantService = plantService;
        this.salesOfferService = salesOfferService;
        this.speciesService = speciesService;
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
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found for id "+id));
        if(categoryDto.getName()!=null)
            category.setName(categoryDto.getName());
        beanValidator.validate(category);
        Category savedCategory = categoryRepository.save(category);
        return mapToDto(savedCategory);
    }

    @Override
    public String delete(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty())
        {
            return "No category with id = "+id;
        }
        List<PlantDto> plants = plantService.getAll();
        plants.stream()
                .filter(p -> p.getCategoryId()==id)
                .forEach(p -> p.setCategoryId(null));
        List<PostDto> postDtos = postService.getAllByCategory(id);
        postDtos.stream().forEach( p -> p.setCategoryId(null));
        List<SalesOfferDto> salesOfferDtos = salesOfferService.getAllByCategory(id);
        salesOfferDtos.stream().forEach(s -> s.setCategoryId(null));
        List<SpeciesDto> speciesDtos = speciesService.getAll();
        speciesDtos.stream().filter(s -> s.getCategoryId()==id)
                .forEach(s -> s.setCategoryId(null));
        categoryRepository.delete(category.get());
        return "Successfully deleted the category with id = "+id;
    }
}
