package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.UserService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BeanValidator beanValidator;

    public UserServiceImpl(UserRepository userRepository, BeanValidator beanValidator) {
        this.userRepository = userRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id "+id));
        return mapToDto(user);
    }

    @Override
    public UserDto add(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByUid(userDto.getUid());
        if(optionalUser.isPresent())
            return mapToDto(optionalUser.get());

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setUid(userDto.getUid());
        user.setEmail(userDto.getEmail());
        user.setPermission(userDto.getPermission());

        beanValidator.validate(user);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id "+id));
        user.setUsername(userDto.getUsername());
        if(userDto.getUid()!=null){
            throw new ImmutableFieldException("Field uid in User is immutable!");
        }

        user.setEmail(userDto.getEmail());
        user.setPermission(userDto.getPermission());
        beanValidator.validate(user);
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
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUid(user.getUid());
        dto.setEmail(user.getEmail());
        dto.setPermission(user.getPermission());
        return dto;
    }
}
