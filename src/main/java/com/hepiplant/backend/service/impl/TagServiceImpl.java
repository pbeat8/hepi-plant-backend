package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.TagDto;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.entity.SalesOffer;
import com.hepiplant.backend.entity.Tag;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.SalesOfferRepository;
import com.hepiplant.backend.repository.TagRepository;
import com.hepiplant.backend.service.TagService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class TagServiceImpl implements TagService {

    private final BeanValidator beanValidator;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final SalesOfferRepository salesOfferRepository;

    public TagServiceImpl(BeanValidator beanValidator, TagRepository tagRepository, PostRepository postRepository, SalesOfferRepository salesOfferRepository) {
        this.beanValidator = beanValidator;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.salesOfferRepository = salesOfferRepository;
    }

    @Override
    public List<TagDto> getAll() {
        return tagRepository.findAll().stream().map(DtoMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TagDto getById(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tag not found for id "+id));
        return mapToDto(tag);
    }

    @Override
    public TagDto getByName(String name) {
        Tag tag = tagRepository.findByName(name.toLowerCase()).orElseThrow(() -> new EntityNotFoundException("Tag not found for name "+name));
        return mapToDto(tag);
    }

    @Override
    public TagDto add(TagDto tagDto) {
        Optional<Tag> optionalTag = tagRepository.findByName(tagDto.getName().toLowerCase().trim());
        if(optionalTag.isPresent())
            return mapToDto(optionalTag.get());
        Tag tag = new Tag();
        tag.setName(tagDto.getName().toLowerCase().trim());
        if(tagDto.getPosts()!=null){
            Set<Post> posts=null;
            for (Long id: tagDto.getPosts()) {
                if(id!=null){
                    Post post = postRepository.findById(id).get();
                    if(post!=null) posts.add(post);
                }

            }
            tag.setPosts(posts);
        }
        if(tagDto.getSalesOffer()!=null){
            Set<SalesOffer> salesOffers=null;
            for (Long id: tagDto.getSalesOffer()) {
                if(id!=null){
                    SalesOffer salesOffer = salesOfferRepository.findById(id).get();
                    if(salesOffer!=null) salesOffers.add(salesOffer);
                }

            }
            tag.setSalesOffers(salesOffers);
        }

        beanValidator.validate(tag);
        Tag savedTag = tagRepository.save(tag);
        return mapToDto(savedTag);
    }

    @Override
    public String delete(Long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        if(tag.isEmpty()){
            return "No tag with id = " + id;
        }
        Tag tagValue = tag.get();
        tagValue.getPosts()
            .forEach(p -> p.getTags().remove(tag));
        tagValue.getSalesOffers()
            .forEach(s -> s.getTags().remove(tag));
        tagRepository.delete(tagValue);
        return "Successfully deleted the tag with id = "+ id;
    }
}
