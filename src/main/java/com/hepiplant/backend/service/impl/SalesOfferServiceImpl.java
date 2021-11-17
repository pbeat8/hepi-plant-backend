package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.SalesOfferDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SalesOfferRepository;
import com.hepiplant.backend.repository.TagRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.SalesOfferService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;
import static com.hepiplant.backend.util.ConversionUtils.convertToLocalDate;

@Service
public class SalesOfferServiceImpl implements SalesOfferService {

    private final SalesOfferRepository salesOfferRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BeanValidator beanValidator;
    private final TagRepository tagRepository;

    public SalesOfferServiceImpl(final SalesOfferRepository salesOfferRepository,
                                 final CategoryRepository categoryRepository,
                                 final UserRepository userRepository,
                                 final BeanValidator beanValidator,
                                 final TagRepository tagRepository) {
        this.salesOfferRepository = salesOfferRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.beanValidator = beanValidator;
        this.tagRepository = tagRepository;
    }

    @Override
    public SalesOfferDto create(SalesOfferDto salesOfferDto) {
        SalesOffer salesOffer = new SalesOffer();
        salesOffer.setTitle(salesOfferDto.getTitle());
        salesOffer.setBody(salesOfferDto.getBody());
        if(salesOfferDto.getTags()!=null){
            addTagsToSalesOffer(salesOffer, salesOfferDto);
        }
        salesOffer.setPhoto(salesOfferDto.getPhoto());
        salesOffer.setLocation(salesOfferDto.getLocation());
        salesOffer.setPrice(salesOfferDto.getPrice());
        // not null
        User user = userRepository.findById(salesOfferDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for id " + salesOfferDto.getUserId()));
        salesOffer.setUser(user);
        // Can it be null?
        Category category = categoryRepository.findById(salesOfferDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + salesOfferDto.getCategoryId()));
        salesOffer.setCategory(category);
        salesOffer.setComments(new ArrayList<>());
        beanValidator.validate(salesOffer);
        SalesOffer savedSalesOffer = salesOfferRepository.save(salesOffer);
        return mapToDto(savedSalesOffer);
    }

    @Override
    public List<SalesOfferDto> getAll(Date startDate, Date endDate) {
       return salesOfferRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate).plusDays(1)).stream()
                        .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                        .map(DtoMapper::mapToDto)
                        .collect(Collectors.toList());

    }

    @Override
    public List<SalesOfferDto> getAllByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + categoryId));
        return salesOfferRepository.findAllByCategory(category).stream()
                .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOfferDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        return salesOfferRepository.findAllByUser(user).stream()
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOfferDto> getAllByTag(String tag) {
        Optional<Tag> tags = tagRepository.findByName(tag);
        if(tags.isPresent())
        return salesOfferRepository.findAll().stream()
                .filter(p -> p.getTags().contains(tags.get()))
                .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
        else return salesOfferRepository.findAll().stream()
                .filter(p -> p.getTags().contains(""))
                .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOfferDto> getAllByFilters(Date startDate, Date endDate, String tag, Long categoryId) {
        Set<SalesOfferDto> salesOfferDtos = new HashSet<>();
        boolean wasInIf = false;
        if(startDate==null && endDate==null && tag==null && categoryId==null){
            salesOfferDtos.addAll(salesOfferRepository.findAll().stream()
                    .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList()));
            wasInIf=true;
        }
        if(startDate!=null && endDate!=null){
            List<SalesOfferDto> temp= getAll(startDate,endDate);
            salesOfferDtos = getSalesOffersDtos(salesOfferDtos, temp, wasInIf);
            wasInIf =true;
        }

        if(tag!=null && !tag.isEmpty()){
            List<SalesOfferDto> temp= getAllByTag(tag);
            salesOfferDtos = getSalesOffersDtos(salesOfferDtos, temp, wasInIf);
            wasInIf=true;
        }

        if(categoryId!=null){
            List<SalesOfferDto> temp = getAllByCategory(categoryId);
            salesOfferDtos = getSalesOffersDtos(salesOfferDtos, temp, wasInIf);
            wasInIf=true;
        }

        List<SalesOfferDto> filterSalesOffers = new ArrayList<>(salesOfferDtos);
        return filterSalesOffers;
    }

    private Set<SalesOfferDto> getSalesOffersDtos(Set<SalesOfferDto> salesOffers, List<SalesOfferDto> temp, boolean was) {
        if (!salesOffers.isEmpty()) {
            Set<Long> indexes = temp.stream().map(SalesOfferDto::getId).collect(Collectors.toSet());
            Set<SalesOfferDto> newPosts = salesOffers.stream().filter(p -> indexes.contains(p.getId())).collect(Collectors.toSet());
            salesOffers = newPosts;
        }
        else if(was && salesOffers.isEmpty()){
            salesOffers = new HashSet<>();
        }
        else salesOffers.addAll(temp);
        return salesOffers;
    }

    @Override
    public SalesOfferDto getById(Long id) {
        SalesOffer salesOffer = salesOfferRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Sales offer not found for id " + id));
        return mapToDto(salesOffer);
    }

    @Override
    public SalesOfferDto update(Long id, SalesOfferDto salesOfferDto) {
        SalesOffer salesOffer = salesOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales offer not found for id " + id));
        if(salesOfferDto.getTitle() != null){
            salesOffer.setTitle(salesOfferDto.getTitle());
        }
        if(salesOfferDto.getBody() != null){
            salesOffer.setBody(salesOfferDto.getBody());
        }
        if(salesOfferDto.getLocation() != null){
            salesOffer.setLocation(salesOfferDto.getLocation());
        }
        if(salesOfferDto.getPrice() != null){
            salesOffer.setPrice(salesOfferDto.getPrice());
        }
        if(salesOfferDto.getTags() != null && !salesOfferDto.getTags().isEmpty()){
            Set<Tag> oldTags = salesOffer.getTags();
            addTagsToSalesOffer(salesOffer, salesOfferDto);
            oldTags = oldTags.stream()
                    .filter(t -> !salesOfferDto.getTags().contains(t.getName()))
                    .collect(Collectors.toSet());
            removeOrphanTags(oldTags);
        }
        if(salesOfferDto.getPhoto()!=null){
            salesOffer.setPhoto(salesOfferDto.getPhoto());
        }
        if(salesOfferDto.getUserId() != null){
            throw new ImmutableFieldException("Cannot change User for Sales offer!");
        }
        if(salesOfferDto.getCategoryId() != null){
            Category category = categoryRepository.findById(salesOfferDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found for id " + salesOfferDto.getCategoryId()));
            salesOffer.setCategory(category);
        }
        beanValidator.validate(salesOffer);
        SalesOffer savedSalesOffer = salesOfferRepository.save(salesOffer);
        return mapToDto(savedSalesOffer);
    }

    @Override
    public String delete(Long id) {
        Optional<SalesOffer> salesOffer = salesOfferRepository.findById(id);
        if(salesOffer.isEmpty()){
            return "No sales offer with id = " + id;
        }
        SalesOffer salesOfferValue = salesOffer.get();
        removeOrphanTags(salesOfferValue.getTags());
        salesOfferRepository.delete(salesOfferValue);
        return "Successfully deleted the sales offer with id = " + id;
    }

    private void addTagsToSalesOffer(SalesOffer salesOffer, SalesOfferDto salesOfferDto) {
        Set<Tag> tags=new HashSet<>();
        for(String tagName : salesOfferDto.getTags()){
            Optional<Tag> tag = tagRepository.findByName(tagName.toLowerCase());
            if(tag.isPresent()) tags.add(tag.get());
            else{
                Tag newTag = new Tag();
                newTag.setName(tagName.toLowerCase());
                beanValidator.validate(newTag);
                Tag savedTag = tagRepository.save(newTag);
                tags.add(savedTag);
            }
        }
        salesOffer.setTags(tags);
    }

    private void removeOrphanTags(Set<Tag> tags) {
        tags.forEach(tag -> {
            long relatedItemsCount = tagRepository.countPostsAndSalesOffersByTagId(tag.getId());
            if(relatedItemsCount < 2){
                tagRepository.delete(tag);
            }
        });
    }
}
