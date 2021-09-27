package com.hepiplant.backend.mapper;

import com.hepiplant.backend.dto.*;
import com.hepiplant.backend.entity.*;

import java.util.ArrayList;
import java.util.List;

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
        if (event.getPlant() != null)
            dto.setPlantId(event.getPlant().getId());
        return dto;
    }

    public static PlantDto mapToDto(Plant plant){
        PlantDto dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPurchaseDate(plant.getPurchaseDate());
        dto.setLocation(plant.getLocation());
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
        return dto;
    }

    public static PostDto mapToDto(Post post){
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        List<String> tags = new ArrayList<>();
        tags.add(post.getTag1());
        tags.add(post.getTag2());
        tags.add(post.getTag3());
        tags.add(post.getTag4());
        tags.add(post.getTag5());
        while (tags.remove(null));
        dto.setTags(tags);
        dto.setCreatedDate(post.getCreatedDate());
        dto.setUpdatedDate(post.getUpdatedDate());
        if(post.getUser() != null){
            dto.setUserId(post.getUser().getId());
        }
        if(post.getCategory() != null){
            dto.setCategoryId(post.getCategory().getId());
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
        List<String> tags = new ArrayList<>();
        tags.add(salesOffer.getTag1());
        tags.add(salesOffer.getTag2());
        tags.add(salesOffer.getTag3());
        while (tags.remove(null));
        dto.setTags(tags);
        dto.setCreatedDate(salesOffer.getCreatedDate());
        dto.setUpdatedDate(salesOffer.getUpdatedDate());
        if(salesOffer.getUser() != null){
            dto.setUserId(salesOffer.getUser().getId());
        }
        if(salesOffer.getCategory() != null){
            dto.setCategoryId(salesOffer.getCategory().getId());
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
        dto.setPermission(user.getPermission());
        return dto;
    }
}
