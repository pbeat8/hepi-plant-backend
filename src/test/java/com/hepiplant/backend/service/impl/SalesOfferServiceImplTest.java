package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.SalesOfferDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.SalesOffer;
import com.hepiplant.backend.entity.Tag;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SalesOfferRepository;
import com.hepiplant.backend.repository.TagRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.BeforeAll;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOfferServiceImplTest {

    @Mock
    private SalesOfferRepository salesOfferRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<SalesOffer> salesOfferArgumentCaptor;

    @InjectMocks
    private SalesOfferServiceImpl salesOfferService;

    private SalesOffer salesOffer;
    private SalesOfferDto dto;
    private static User user;
    private static Category category;
    private static Tag tag;
    private static Set<Tag> tags;

    @BeforeAll
    public static void initializeVariables(){
        user = new User(1L, "username1", "uId1", "email@gmail.com",
                true, "00:00:00", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        category = new Category(2L, "Category1", new ArrayList<>());
        tag = new Tag(1L,"tag1",new HashSet<>(),new HashSet<>());
        tags =new HashSet<>();
        tags.add(tag);
    }

    @BeforeEach
    public void initializeSalesOffer(){
        salesOffer = new SalesOffer(1L,"Title 1", "Body 1","Wroclaw", BigDecimal.valueOf(23.88),"photo", user, category,tags);
        dto = new SalesOfferDto();
        dto.setTitle(salesOffer.getTitle());
        dto.setBody(salesOffer.getBody());
        dto.setLocation(salesOffer.getLocation());
        dto.setPrice(salesOffer.getPrice());
        dto.setTags(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setUserId(user.getId());
        dto.setCategoryId(category.getId());
    }

    // CREATE tests
    @Test
    public void shouldCreateSalesOfferOk(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));
        given(salesOfferRepository.save(salesOfferArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        SalesOfferDto result = salesOfferService.create(dto);

        //then
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(times(1)).findByName(eq((String) dto.getTags().toArray()[0]));
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferRepository).should(times(1)).save(any(SalesOffer.class));
        assertEquals(salesOffer.getTitle(), result.getTitle());
        assertEquals(salesOffer.getBody(), result.getBody());
        assertEquals(salesOffer.getLocation(), result.getLocation());
        assertEquals(salesOffer.getPrice(), result.getPrice());
        assertEquals(1, result.getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.getTags());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(category.getId(), result.getCategoryId());

        SalesOffer captorValue = salesOfferArgumentCaptor.getValue();
        assertEquals(salesOffer.getTitle(), captorValue.getTitle());
        assertEquals(salesOffer.getBody(), captorValue.getBody());
        assertEquals(salesOffer.getLocation(), captorValue.getLocation());
        assertEquals(salesOffer.getPrice(), captorValue.getPrice());
        assertEquals(salesOffer.getTags(), captorValue.getTags());

        assertEquals(user, captorValue.getUser());
        assertEquals(category, captorValue.getCategory());
    }

    @Test
    public void shouldCreateSalesOfferUserDoesNotExistThrowsException(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(atMostOnce()).save(tag);
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    @Test
    public void shouldCreateSalesOfferCategoryDoesNotExistThrowsException(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(atMostOnce()).save(tag);
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    @Test
    public void shouldCreateSalesOfferInvalidValuesThrowsException(){
        //given
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> salesOfferService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(times(1)).findByName((String) dto.getTags().toArray()[0]);
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    // GET ALL tests
    @Test
    public void shouldGetAllSalesOffersOk(){
        //given
        given(salesOfferRepository.findAll()).willReturn(List.of(salesOffer));

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(null, null, null, null);

        //then
        then(salesOfferRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(salesOffer.getTitle(), result.get(0).getTitle());
        assertEquals(salesOffer.getBody(), result.get(0).getBody());
        assertEquals(salesOffer.getLocation(), result.get(0).getLocation());
        assertEquals(salesOffer.getPrice(), result.get(0).getPrice());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllSalesOffersEmptyListOk(){
        //given
        given(salesOfferRepository.findAll()).willReturn(List.of());

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(null, null,null,null);

        //then
        then(salesOfferRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    // GET ALL with dates tests
    @Test
    public void shouldGetAllSalesOffersByDateOk() throws ParseException {
        //given
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date startDate = formatter.parse("01-01-2021");
        Date endDate = formatter.parse("30-03-2021");
        given(salesOfferRepository.findAllByCreatedDateBetween(any(), any()))
                .willReturn(List.of(salesOffer));

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(startDate, endDate,null,null);

        //then
        then(salesOfferRepository).should(times(1)).findAllByCreatedDateBetween(any(), any());
        assertEquals(1, result.size());
        assertEquals(salesOffer.getTitle(), result.get(0).getTitle());
        assertEquals(salesOffer.getBody(), result.get(0).getBody());
        assertEquals(salesOffer.getLocation(), result.get(0).getLocation());
        assertEquals(salesOffer.getPrice(), result.get(0).getPrice());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllSalesOffersByDateEmptyListOk() throws ParseException {
        //given
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date startDate = formatter.parse("01-01-2021");
        Date endDate = formatter.parse("30-03-2021");
        given(salesOfferRepository.findAllByCreatedDateBetween(any(), any()))
                .willReturn(List.of());

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(startDate,endDate,null,null);

        //then
        then(salesOfferRepository).should(times(1)).findAllByCreatedDateBetween(any(), any());
        assertEquals(0, result.size());
    }

    // GET ALL BY USER tests

    @Test
    public void shouldGetAllSalesOfferByUserOk(){
        //given
        given(userRepository.findById(salesOffer.getUser().getId())).willReturn(Optional.of(user));
        given(salesOfferRepository.findAllByUser(salesOffer.getUser())).willReturn(List.of(salesOffer));

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByUser(salesOffer.getUser().getId());

        //then
        then(userRepository).should(times(1)).findById(dto.getUserId());
        then(salesOfferRepository).should(times(1)).findAllByUser(any(User.class));
        assertEquals(1, result.size());
        assertEquals(salesOffer.getTitle(), result.get(0).getTitle());
        assertEquals(salesOffer.getBody(), result.get(0).getBody());
        assertEquals(salesOffer.getLocation(), result.get(0).getLocation());
        assertEquals(salesOffer.getPrice(), result.get(0).getPrice());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllSalesOffersByUserEmptyListOk(){
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(salesOfferRepository.findAllByUser(salesOffer.getUser())).willReturn(List.of());

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByUser(anyLong());

        //then
        then(userRepository).should(times(1)).findById(anyLong());
        then(salesOfferRepository).should(times(1)).findAllByUser(any(User.class));
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllSalesOffersByUserIdUserDoesNotExistThrowsException(){
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.getAllByUser(anyLong()));
        then(userRepository).should(times(1)).findById(anyLong());
        then(salesOfferRepository).should(times(0)).findAllByUser(any(User.class));
    }

    @Test
    public void shouldGetAllSalesOffersByCategoryOk(){
        //given
        given(categoryRepository.findById(salesOffer.getCategory().getId())).willReturn(Optional.of(category));
        given(salesOfferRepository.findAllByCategory(salesOffer.getCategory())).willReturn(List.of(salesOffer));

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(null,null,null,salesOffer.getCategory().getId());

        //then
        then(categoryRepository).should(times(1)).findById(dto.getCategoryId());
        then(salesOfferRepository).should(times(1)).findAllByCategory(any(Category.class));
        assertEquals(1, result.size());
        assertEquals(salesOffer.getTitle(), result.get(0).getTitle());
        assertEquals(salesOffer.getBody(), result.get(0).getBody());
        assertEquals(salesOffer.getLocation(), result.get(0).getLocation());
        assertEquals(salesOffer.getPrice(), result.get(0).getPrice());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllSalesOffersByCategoryEmptyListOk(){
        //given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(salesOfferRepository.findAllByCategory(salesOffer.getCategory())).willReturn(List.of());

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(null,null,null,anyLong());

        //then
        then(categoryRepository).should(times(1)).findById(anyLong());
        then(salesOfferRepository).should(times(1)).findAllByCategory(any(Category.class));
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetAllSalesOffersByCategoryIdDoesNotExistThrowsException(){
        //given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.getAllByCategory(anyLong()));
        then(categoryRepository).should(times(1)).findById(anyLong());
        then(salesOfferRepository).should(times(0)).findAllByCategory(any(Category.class));
    }

    // GET ALL BY TAG tests
    @Test
    public void shouldGetAllSalesOffersByTagOk(){
        //given
        given(salesOfferRepository.findAll()).willReturn(List.of(salesOffer));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByFilters(null,null,(String) salesOffer.getTags().stream().map(Tag::getName).distinct().toArray()[0],null);

        //then
        then(tagRepository).should(times(1)).findByName((String) dto.getTags().toArray()[0]);
        then(salesOfferRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(salesOffer.getTitle(), result.get(0).getTitle());
        assertEquals(salesOffer.getBody(), result.get(0).getBody());
        assertEquals(salesOffer.getLocation(), result.get(0).getLocation());
        assertEquals(salesOffer.getPrice(), result.get(0).getPrice());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.get(0).getTags());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(category.getId(), result.get(0).getCategoryId());
    }

    @Test
    public void shouldGetAllSalesOffersByTagEmptyListOk(){
        //given
        given(salesOfferRepository.findAll()).willReturn(List.of(salesOffer));

        //when
        List<SalesOfferDto> result = salesOfferService.getAllByTag("someTag");

        //then
        then(salesOfferRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    // GET BY ID tests
    @Test
    public void shouldGetSalesOfferByIdOk(){
        //given
        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.of(salesOffer));

        //when
        SalesOfferDto result = salesOfferService.getById(salesOffer.getId());

        //then
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        assertEquals(salesOffer.getTitle(), result.getTitle());
        assertEquals(salesOffer.getBody(), result.getBody());
        assertEquals(salesOffer.getLocation(), result.getLocation());
        assertEquals(salesOffer.getPrice(), result.getPrice());
        assertEquals(1, result.getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.getTags());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(category.getId(), result.getCategoryId());
    }

    @Test
    public void shouldGetSalesOfferByIdSalesOfferDoesNotExistThrowsException(){
        //given
        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.getById(salesOffer.getId()));
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
    }

    // UPDATE tests

    @Test
    public void shouldUpdateSalesOfferOk(){
        //given
        SalesOffer salesOfferToUpdate = new SalesOffer();
        salesOfferToUpdate.setTags(new HashSet<>());
        dto.setUserId(null);
        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.of(salesOfferToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));
        given(salesOfferRepository.save(salesOfferArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        SalesOfferDto result = salesOfferService.update(salesOffer.getId(), dto);

        //then
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(times(1)).findByName(eq((String) dto.getTags().toArray()[0]));
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferRepository).should(times(1)).save(any(SalesOffer.class));
        assertEquals(salesOffer.getTitle(), result.getTitle());
        assertEquals(salesOffer.getBody(), result.getBody());
        assertEquals(salesOffer.getLocation(), result.getLocation());
        assertEquals(salesOffer.getPrice(), result.getPrice());
        assertEquals(1, result.getTags().size());
        assertEquals(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()), result.getTags());
        assertEquals(category.getId(), result.getCategoryId());

        SalesOffer captorValue = salesOfferArgumentCaptor.getValue();
        assertEquals(salesOffer.getTitle(), captorValue.getTitle());
        assertEquals(salesOffer.getBody(), captorValue.getBody());
        assertEquals(salesOffer.getLocation(), captorValue.getLocation());
        assertEquals(salesOffer.getPrice(), captorValue.getPrice());
        assertEquals(salesOffer.getTags(), captorValue.getTags());
        assertEquals(category, captorValue.getCategory());
    }

    @Test
    public void shouldUpdateSalesOfferDoesNotExistThrowsException(){
        //given
        dto.setUserId(null);

        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.update(salesOffer.getId(), dto));
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    @Test
    public void shouldUpdateSalesOfferCategoryDoesNotExistThrowsException(){
        //given
        SalesOffer salesOfferToUpdate = new SalesOffer();
        salesOfferToUpdate.setTags(new HashSet<>());
        dto.setUserId(null);

        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.of(salesOfferToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> salesOfferService.update(salesOffer.getId(), dto));
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    @Test
    public void shouldUpdateSalesOfferUserChangeThrowsException(){
        //given
        SalesOffer salesOfferToUpdate = new SalesOffer();
        salesOfferToUpdate.setTags(new HashSet<>());

        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.of(salesOfferToUpdate));

        //when

        //then
        assertThrows(ImmutableFieldException.class, () -> salesOfferService.update(salesOffer.getId(), dto));
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    @Test
    public void shouldUpdateSalesOfferInvalidValuesThrowsException(){
        //given
        SalesOffer salesOfferToUpdate = new SalesOffer();
        salesOfferToUpdate.setTags(new HashSet<>());
        dto.setUserId(null);
        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.of(salesOfferToUpdate));
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(tagRepository.findByName((String) dto.getTags().toArray()[0])).willReturn(Optional.of(tag));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> salesOfferService.update(salesOffer.getId(), dto));
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(tagRepository).should(atMostOnce()).findByName(eq((String) dto.getTags().toArray()[0]));
        then(beanValidator).should(times(1)).validate(any());
        then(salesOfferRepository).should(times(0)).save(any(SalesOffer.class));
    }

    // DELETE tests
    @Test
    public void shouldDeleteSalesOfferOk(){
        //given
        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.of(salesOffer));

        //when
        String result = salesOfferService.delete(salesOffer.getId());

        //then
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(salesOfferRepository).should(times(1)).delete(salesOffer);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeleteSalesOfferDoesNotExistThrowsException(){
        //given
        given(salesOfferRepository.findById(salesOffer.getId())).willReturn(Optional.empty());

        //when
        String result = salesOfferService.delete(salesOffer.getId());

        //then
        then(salesOfferRepository).should(times(1)).findById(salesOffer.getId());
        then(salesOfferRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No sales offer"));
    }

}
