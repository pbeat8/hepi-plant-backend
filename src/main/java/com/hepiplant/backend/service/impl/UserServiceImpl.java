package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.UserService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapToDto(user);
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = new User();
        if(userDto.getUsername()!=null)
            user.setUsername(userDto.getUsername());
        if(userDto.getLogin()!=null)
            user.setLogin(userDto.getLogin());
        if(userDto.getPassword()!=null)
            user.setPassword(userDto.getPassword());
        if(userDto.getEmail()!=null)
            user.setEmail(userDto.getEmail());
        if(userDto.getPermission()!=null)
            user.setPermission(userDto.getPermission());
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {

        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(userDto.getUsername()!=null && !userDto.getUsername().isEmpty())
            user.setUsername(userDto.getUsername());
        if(userDto.getLogin()!=null && !userDto.getLogin().isEmpty())
            user.setLogin(userDto.getLogin());
        if(userDto.getPassword()!=null && !userDto.getPassword().isEmpty())
            user.setPassword(userDto.getPassword());
        if(userDto.getEmail()!=null && !userDto.getEmail().isEmpty())
            user.setEmail(userDto.getEmail());
        if(userDto.getPermission()!=null)
            user.setPermission(userDto.getPermission());
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public String delete(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            return "No user with id = " + id;
        }
        userRepository.delete(user.get());
        return "Successfully deleted the user with id = "+ id;
    }
    private UserDto mapToDto(User user){
        UserDto dto = new UserDto();

        if(user.getUsername()!=null)
            dto.setUsername(user.getUsername());
        if(user.getLogin()!=null)
            dto.setLogin(user.getLogin());
        if(user.getPassword()!=null)
            dto.setPassword(user.getPassword());
        if(user.getEmail()!=null)
            dto.setEmail(user.getEmail());
        if(user.getPermission()!=null)
            dto.setPermission(user.getPermission());
        return dto;
    }
}
