package com.hepiplant.backend.mapper;

import com.hepiplant.backend.dto.*;
import com.hepiplant.backend.entity.*;

import java.util.stream.Collectors;

public class DtoMapper {

    public static CategoryDto mapToDto(Category category)
    {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public static EventDto mapToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventDate(event.getEventDate());
        dto.setDone(event.isDone());
        if (event.getPlant() != null){
            dto.setPlantId(event.getPlant().getId());
            dto.setPlantName(event.getPlant().getName());
        }
        return dto;
    }

    public static PlantDto mapToDto(Plant plant){
        PlantDto dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPurchaseDate(plant.getPurchaseDate());
        dto.setLocation(plant.getLocation());
        dto.setPhoto(plant.getPhoto());
        if(plant.getCategory() != null) {
            dto.setCategoryId(plant.getCategory().getId());
        }
        if(plant.getSpecies() != null) {
            dto.setSpecies(mapToDto(plant.getSpecies()));
        }
        if(plant.getUser() != null){
            dto.setUserId(plant.getUser().getId());
        }
        if(plant.getSchedule() != null){
            dto.setSchedule(mapToDto(plant.getSchedule()));
        }
        if(plant.getEventList()!=null){
            dto.setEvents(plant.getEventList().stream()
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static PostDto mapToDto(Post post){
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        if(post.getTags() != null){
            dto.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        }
        dto.setPhoto(post.getPhoto());
        dto.setCreatedDate(post.getCreatedDate());
        dto.setUpdatedDate(post.getUpdatedDate());
        if(post.getUser() != null){
            dto.setUserId(post.getUser().getId());
            dto.setUsername(post.getUser().getUsername());
        }
        if(post.getCategory() != null){
            dto.setCategoryId(post.getCategory().getId());
        }
        if(post.getComments() != null) {
            dto.setComments(post.getComments().stream()
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static CommentDto mapToDto(PostComment postComment){
        CommentDto dto = new CommentDto();
        dto.setId(postComment.getId());
        dto.setBody(postComment.getBody());
        dto.setCreatedDate(postComment.getCreatedDate());
        dto.setUpdatedDate(postComment.getUpdatedDate());
        if(postComment.getUser() != null){
            dto.setUserId(postComment.getUser().getId());
            dto.setUsername(postComment.getUser().getUsername());
        }
        if(postComment.getPost() != null){
            dto.setPostId(postComment.getPost().getId());
        }
        return dto;
    }

    public static SalesOfferDto mapToDto(SalesOffer salesOffer){
        SalesOfferDto dto = new SalesOfferDto();
        dto.setId(salesOffer.getId());
        dto.setTitle(salesOffer.getTitle());
        dto.setBody(salesOffer.getBody());
        dto.setLocation(salesOffer.getLocation());
        dto.setPrice(salesOffer.getPrice());
        if(salesOffer.getTags()!=null){
            dto.setTags(salesOffer.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        }

        dto.setPhoto(salesOffer.getPhoto());
        dto.setCreatedDate(salesOffer.getCreatedDate());
        dto.setUpdatedDate(salesOffer.getUpdatedDate());
        if(salesOffer.getUser() != null){
            dto.setUserId(salesOffer.getUser().getId());
            dto.setUsername(salesOffer.getUser().getUsername());
        }
        if(salesOffer.getCategory() != null){
            dto.setCategoryId(salesOffer.getCategory().getId());
        }
        if(salesOffer.getComments() != null) {
            dto.setComments(salesOffer.getComments().stream()
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static CommentDto mapToDto(SalesOfferComment salesOfferComment){
        CommentDto dto = new CommentDto();
        dto.setId(salesOfferComment.getId());
        dto.setBody(salesOfferComment.getBody());
        dto.setCreatedDate(salesOfferComment.getCreatedDate());
        dto.setUpdatedDate(salesOfferComment.getUpdatedDate());
        if(salesOfferComment.getUser() != null){
            dto.setUserId(salesOfferComment.getUser().getId());
            dto.setUsername(salesOfferComment.getUser().getUsername());
        }
        if(salesOfferComment.getSalesOffer() != null){
            dto.setPostId(salesOfferComment.getSalesOffer().getId());
        }
        return dto;
    }

    public static ScheduleDto mapToDto(Schedule schedule){
        ScheduleDto dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setWateringFrequency(schedule.getWateringFrequency());
        dto.setFertilizingFrequency(schedule.getFertilizingFrequency());
        dto.setMistingFrequency(schedule.getMistingFrequency());
        if(schedule.getPlant() != null){
            dto.setPlantId(schedule.getPlant().getId());
        }
        return dto;
    }

    public static SpeciesDto mapToDto(Species species){
        SpeciesDto dto = new SpeciesDto();
        dto.setId(species.getId());
        dto.setName(species.getName());
        dto.setWateringFrequency(species.getWateringFrequency());
        dto.setFertilizingFrequency(species.getFertilizingFrequency());
        dto.setMistingFrequency(species.getMistingFrequency());
        dto.setPlacement(species.getPlacement());
        dto.setSoil(species.getSoil());
        if(species.getCategory() != null){
            dto.setCategoryId(species.getCategory().getId());
        }
        return dto;
    }

    public static UserDto mapToDto(User user){
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUid(user.getUid());
        dto.setEmail(user.getEmail());
        dto.setNotifications(user.isNotifications());
        dto.setHourOfNotifications(user.getHourOfNotifications());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }

    public static TagDto mapToDto(Tag tag){
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName().toLowerCase().trim());
        if(tag.getPosts()!=null)
            dto.setPosts(tag.getPosts().stream().map(Post::getId).collect(Collectors.toSet()));
        if(tag.getSalesOffers()!=null)
            dto.setSalesOffers(tag.getSalesOffers().stream().map(SalesOffer::getId).collect(Collectors.toSet()));
        return dto;
    }

}
