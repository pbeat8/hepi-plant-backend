package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private BeanValidator beanValidator;

    @Test
    void shouldGetAllOk() {
        //given
        User user = new User(1L, "username 1", "login 1", "password 1", "email 1", null, null, null, null);
        given(userRepository.findAll()).willReturn(List.of(user));

        //when
        List<UserDto> result = userService.getAll();
        //then
        then(userRepository).should(times(1)).findAll();

        assertEquals(1,result.size());
        assertEquals("username 1",result.get(0).getUsername());
        assertEquals("login 1",result.get(0).getLogin());
        assertEquals("password 1",result.get(0).getPassword());
        assertEquals("email 1",result.get(0).getEmail());
    }

    @Test
    void shouldGetByIdOk() {
        //given
        User user = new User(1L, "username 1", "login 1", "password 1", "email 1", null, null, null, null);
        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(user));

        //when
        UserDto result = userService.getById(1L);
        //then
        then(userRepository).should(times(1)).findById(1L);

        assertEquals("username 1",result.getUsername());
        assertEquals("login 1",result.getLogin());
        assertEquals("password 1",result.getPassword());
        assertEquals("email 1",result.getEmail());
    }

    @Test
    void shouldGetByIdThrow(){

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getById(-1L);
        });

        String expectedMessage = "User not found for id -1";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);

    }

    @Test
    void shouldAddOk() {
        //given
        User user = new User(1L, "username 1", "login 1", "password 1", "email 1", null, null, null, null);

        //when
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setLogin(user.getLogin());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setPermission(user.getPermission());
        UserDto result = userService.add(userDto);

        //then
        then(userRepository).should(times(1)).save(any(User.class));

        assertEquals("username 1",result.getUsername());
        assertEquals("login 1",result.getLogin());
        assertEquals("password 1",result.getPassword());
        assertEquals("email 1",result.getEmail());


    }

    @Test
    void shouldUpdateOk() {
    }

    @Test
    void shouldDeleteOk() {
        //given
        User user = new User(1L, "username 1", "login 1", "password 1", "email 1", null, null, null, null);
        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(user));

        //when
        String result = userService.delete(1L);

        //then
        then(userRepository).should(times(1)).delete(user);
        assertEquals("Successfully deleted the user with id = 1",result);
    }
}