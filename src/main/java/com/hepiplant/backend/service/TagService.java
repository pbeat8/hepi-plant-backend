package com.hepiplant.backend.service;
import com.hepiplant.backend.dto.TagDto;
import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.entity.Tag;

import java.util.List;

public interface TagService {

    List<TagDto> getAll();
    TagDto getByName(String name);
    TagDto add(TagDto tagDto);
    String delete(Long id);

}
