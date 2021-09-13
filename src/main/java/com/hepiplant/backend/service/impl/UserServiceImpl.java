package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.entity.User;
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
        Optional<User> optionalUser = userRepository.findByuId(userDto.getuId());
        if(optionalUser.isPresent()) return mapToDto(optionalUser.get());
        User user = new User();
        if(userDto.getUsername()!=null)
            user.setUsername(userDto.getUsername());
        if(userDto.getuId()!=null)
            user.setuId(userDto.getuId());
        if(userDto.getEmail()!=null)
            user.setEmail(userDto.getEmail());
        if(userDto.getPermission()!=null)
            user.setPermission(userDto.getPermission());
        beanValidator.validate(user);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id "+id));
        if(userDto.getUsername()!=null && !userDto.getUsername().isEmpty())
            user.setUsername(userDto.getUsername());
        if(userDto.getuId()!=null && !userDto.getuId().isEmpty())
            user.setuId(userDto.getuId());
        if(userDto.getEmail()!=null && !userDto.getEmail().isEmpty())
            user.setEmail(userDto.getEmail());
        if(userDto.getPermission()!=null)
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
        dto.setuId(user.getuId());
        dto.setEmail(user.getEmail());
        dto.setPermission(user.getPermission());
        return dto;
    }
}
