package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BeanValidator beanValidator;
    @Captor
    private ArgumentCaptor<Category> categoryArgumentCaptor;

    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;
    private CategoryDto dto;

    @BeforeEach
    public void initializeCategory(){
        category = new Category(1L, "Category", new ArrayList<>());
        category.setPlantList(new ArrayList<>());
        category.setSpeciesList(new ArrayList<>());
        category.setPostList(new ArrayList<>());
        category.setSalesOfferList(new ArrayList<>());
        dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
    }

    //Create tests
    @Test
    public void shouldCreateCategoryOk(){
        //given
        given(categoryRepository.save(categoryArgumentCaptor.capture())).willAnswer(returnsFirstArg());
        //when
        CategoryDto result = categoryService.add(dto);
        //then
        then(beanValidator).should(times(1)).validate(any());
        then(categoryRepository).should(times(1)).save(any(Category.class));
        assertEquals(category.getName(), result.getName());

        Category captorValue = categoryArgumentCaptor.getValue();
        assertEquals(category.getName(), captorValue.getName());
    }

    @Test
    public void sholudCreateCategoryInvalidValuesThrowsException(){
        //given
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());
        //when
        //then
        assertThrows(InvalidBeanException.class, () -> categoryService.add(dto));
        then(beanValidator).should(times(1)).validate(any());
        then(categoryRepository).should(times(0)).save(any(Category.class));
    }

    @Test
    public void shouldGetAllCategoriesOk(){
        //given
        given(categoryRepository.findAll()).willReturn(List.of(category));

        //when
        List<CategoryDto> result = categoryService.getAll();

        //then
        then(categoryRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(category.getName(), result.get(0).getName());
    }

    @Test
    public void shouldGetAllCategoriesEmptyListOk()
    {
        //given
        given(categoryRepository.findAll()).willReturn(List.of());

        //when
        List<CategoryDto> result = categoryService.getAll();

        //then
        then(categoryRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetCategoryByIdOk()
    {
        //given
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        //when
        CategoryDto result = categoryService.getById(category.getId());

        //then
        then(categoryRepository).should(times(1)).findById(category.getId());
        assertEquals(category.getName(), result.getName());
    }

    @Test
    public void shouldGetCategoryByIdDoesNotExistThrowsException()
    {
        //given
        given(categoryRepository.findById(category.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(category.getId()));
        then(categoryRepository).should(times(1)).findById(category.getId());
    }

    @Test
    public void shouldUpdateCategoryOk()
    {
        //given
        Category categoryToUpdate = new Category();
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(categoryToUpdate));
        given(categoryRepository.save(categoryArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        CategoryDto result = categoryService.update(category.getId(), dto);

        //then
        then(categoryRepository).should(times(1)).findById(category.getId());
        then(beanValidator).should(times(1)).validate(any());
        assertEquals(category.getName(), result.getName());

        Category captorValue = categoryArgumentCaptor.getValue();
        assertEquals(category.getName(), captorValue.getName());
    }

    @Test
    public void shouldUpdateCategoryInvalidValuesThrowsException()
    {
        //given
        Category categoryToUpdate = new Category();
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(categoryToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> categoryService.update(category.getId(), dto));
        then(categoryRepository).should(times(1)).findById(category.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(categoryRepository).should(times(0)).save(any(Category.class));
    }

    @Test
    public void shouldDeleteCategoryOk(){
        //given
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        //when
        String result = categoryService.delete(category.getId());

        //then
        then(categoryRepository).should(times(1)).findById(category.getId());
        then(categoryRepository).should(times(1)).delete(category);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeleteCategoryDoesNotExistThrowsException()
    {
        //given
        given(categoryRepository.findById(category.getId())).willReturn(Optional.empty());

        //when
        String result = categoryService.delete(category.getId());

        //then
        then(categoryRepository).should(times(1)).findById(category.getId());
        then(categoryRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No category"));
    }
}
