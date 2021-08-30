package com.hepiplant.backend.service;
import com.hepiplant.backend.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    UserDto getById(Long id);
    UserDto add(UserDto userDto);
    UserDto update(Long id, UserDto userDto);
    String delete(Long id);
}
