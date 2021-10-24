package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.SalesOfferDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.SalesOffer;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SalesOfferRepository;
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

    private static final int TAGS_AMOUNT = 3;

    private final SalesOfferRepository salesOfferRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BeanValidator beanValidator;

    public SalesOfferServiceImpl(SalesOfferRepository salesOfferRepository, CategoryRepository categoryRepository, UserRepository userRepository, BeanValidator beanValidator) {
        this.salesOfferRepository = salesOfferRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public SalesOfferDto create(SalesOfferDto salesOfferDto) {
        SalesOffer salesOffer = new SalesOffer();
        salesOffer.setTitle(salesOfferDto.getTitle());
        salesOffer.setBody(salesOfferDto.getBody());
        if(salesOfferDto.getTags()!=null){
            addTagsToSalesOffer(salesOffer, salesOfferDto.getTags());
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
        if (startDate == null || endDate == null) { // todo maybe change
            return salesOfferRepository.findAll().stream()
                    .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
        } else {
            return salesOfferRepository.findAllByCreatedDateBetween(convertToLocalDate(startDate), convertToLocalDate(endDate)).stream()
                    .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList());
        }
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
                .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesOfferDto> getAllByTag(String tag) {
        return salesOfferRepository.findAll().stream()
                .filter(p -> tag.equals(p.getTag1()) ||
                        tag.equals(p.getTag2()) ||
                        tag.equals(p.getTag3()))
                .sorted(Comparator.comparing(SalesOffer::getCreatedDate))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
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
            addTagsToSalesOffer(salesOffer, salesOfferDto.getTags());
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
        salesOfferRepository.delete(salesOffer.get());
        return "Successfully deleted the sales offer with id = "+ id;
    }

    private void addTagsToSalesOffer(SalesOffer salesOffer, List<String> tags) {
        for (int i = 0; i < TAGS_AMOUNT; i++) {
            String tag = null;
            if (i < tags.size()) {
                tag = tags.get(i);
            }
            switch (i) {
                case 0:
                    salesOffer.setTag1(tag);
                    break;
                case 1:
                    salesOffer.setTag2(tag);
                    break;
                case 2:
                    salesOffer.setTag3(tag);
                    break;
            }
        }
    }
}
