package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.TagDto;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.SalesOffer;
import com.hepiplant.backend.entity.Tag;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.SalesOfferRepository;
import com.hepiplant.backend.repository.TagRepository;
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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private SalesOfferRepository salesOfferRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private BeanValidator beanValidator;
    @Captor
    private ArgumentCaptor<Tag> tagArgumentCaptor;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag;
    private TagDto dto;
    private static Post post;
    private static SalesOffer salesOffer;

    @BeforeEach
    public void initializeTag(){
        tag = new Tag(1L, "Tag", new HashSet<>(), new HashSet<>());
        post = new Post(1L,"title","body","photo",null,null, Collections.singleton(tag));
        salesOffer = new SalesOffer(1L,"title","body","location", new BigDecimal("0.00"),"photo",null,null, Collections.singleton(tag));
        tag.setPosts(Collections.singleton(post));
        tag.setSalesOffers(Collections.singleton(salesOffer));
        dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setPosts(tag.getPosts().stream().map(Post::getId).collect(Collectors.toSet()));
        dto.setSalesOffers(tag.getSalesOffers().stream().map(SalesOffer::getId).collect(Collectors.toSet()));
    }

    //Create tests
    @Test
    public void shouldCreateTagOk(){

        //given
        given(tagRepository.findByName(dto.getName().toLowerCase())).willReturn(Optional.empty());
        given(postRepository.findById((Long) dto.getPosts().toArray()[0])).willReturn(Optional.of(post));
        given(salesOfferRepository.findById((Long) dto.getSalesOffers().toArray()[0])).willReturn(Optional.of(salesOffer));
        given(tagRepository.save(tagArgumentCaptor.capture())).willAnswer(returnsFirstArg());
        //when
        TagDto result = tagService.add(dto);
        //then
        then(tagRepository).should(times(1)).findByName(dto.getName().toLowerCase());
        then(postRepository).should(times(1)).findById((Long) dto.getPosts().toArray()[0]);
        then(salesOfferRepository).should(times(1)).findById((Long) dto.getSalesOffers().toArray()[0]);
        then(beanValidator).should(times(1)).validate(any());
        then(tagRepository).should(times(1)).save(any(Tag.class));
        assertEquals(tag.getName().toLowerCase().trim(), result.getName());

        Tag captorValue = tagArgumentCaptor.getValue();
        assertEquals(tag.getName().toLowerCase().trim(), captorValue.getName());
    }

    @Test
    public void shouldCreateTagWithNameAlreadyExistsOk(){
        //given
        given(tagRepository.findByName(tag.getName().toLowerCase())).willReturn(Optional.of(tag));

        //when
        TagDto result = tagService.add(dto);
        //then
        then(tagRepository).should(times(1)).findByName(tag.getName().toLowerCase());
        then(tagRepository).should(times(0)).save(any(Tag.class));
        assertEquals(tag.getName().toLowerCase().trim(), result.getName());

    }

    @Test
    public void shouldCreateTagInvalidValuesThrowsException(){
        //given
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());
        //when
        //then
        assertThrows(InvalidBeanException.class, () -> tagService.add(dto));
        then(beanValidator).should(times(1)).validate(any());
        then(tagRepository).should(times(0)).save(any(Tag.class));
    }

    @Test
    public void shouldGetAllCategoriesOk(){
        //given
        given(tagRepository.findAll()).willReturn(List.of(tag));

        //when
        List<TagDto> result = tagService.getAll();

        //then
        then(tagRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(tag.getName().toLowerCase().trim(), result.get(0).getName());
    }

    @Test
    public void shouldGetAllCategoriesEmptyListOk()
    {
        //given
        given(tagRepository.findAll()).willReturn(List.of());

        //when
        List<TagDto> result = tagService.getAll();

        //then
        then(tagRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetTagByIdOk()
    {
        //given
        given(tagRepository.findById(tag.getId())).willReturn(Optional.of(tag));

        //when
        TagDto result = tagService.getById(tag.getId());

        //then
        then(tagRepository).should(times(1)).findById(tag.getId());
        assertEquals(tag.getName().toLowerCase().trim(), result.getName());
    }

    @Test
    public void shouldGetTagByIdDoesNotExistThrowsException()
    {
        //given
        given(tagRepository.findById(tag.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> tagService.getById(tag.getId()));
        then(tagRepository).should(times(1)).findById(tag.getId());
    }

    @Test
    public void shouldGetTagByNameOk()
    {
        //given
        given(tagRepository.findByName(dto.getName().toLowerCase())).willReturn(Optional.of(tag));

        //when
        TagDto result = tagService.getByName(tag.getName().toLowerCase());

        //then
        then(tagRepository).should(times(1)).findByName(eq(dto.getName().toLowerCase()));
        assertEquals(tag.getName().toLowerCase().trim(), result.getName());
    }

    @Test
    public void shouldGetTagByNameDoesNotExistThrowsException()
    {
        //given
        given(tagRepository.findByName(dto.getName().toLowerCase())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> tagService.getByName(tag.getName().toLowerCase()));
        then(tagRepository).should(times(1)).findByName(dto.getName().toLowerCase());
    }

    @Test
    public void shouldDeleteTagOk(){
        tag.setPosts(new HashSet<>());
        tag.setSalesOffers(new HashSet<>());
        //given
        given(tagRepository.findById(tag.getId())).willReturn(Optional.of(tag));

        //when
        String result = tagService.delete(tag.getId());

        //then
        then(tagRepository).should(times(1)).findById(tag.getId());
        then(tagRepository).should(times(1)).delete(tag);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeleteTagDoesNotExistThrowsException() {

        //given
        given(tagRepository.findById(tag.getId())).willReturn(Optional.empty());

        //when
        String result = tagService.delete(tag.getId());

        //then
        then(tagRepository).should(times(1)).findById(tag.getId());
        then(tagRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No tag"));
    }
}
