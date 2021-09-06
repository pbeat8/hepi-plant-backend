package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto dto;

    @BeforeEach
    public void initializeUser(){
        user = new User(1L, "username 1", "login 1", "password 1", "email 1", null, null, null, null);
        dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setPermission(user.getPermission());
    }

    @Test
    void shouldGetAllUsersOk() {
        //given
        given(userRepository.findAll()).willReturn(List.of(user));

        //when
        List<UserDto> result = userService.getAll();

        //then
        then(userRepository).should(times(1)).findAll();

        assertEquals(1,result.size());
        assertEquals(user.getUsername(),result.get(0).getUsername());
        assertEquals(user.getLogin(),result.get(0).getLogin());
        assertEquals(user.getPassword(),result.get(0).getPassword());
        assertEquals(user.getEmail(),result.get(0).getEmail());
    }

    @Test
    public void shouldGetAllUsersEmptyListOk()
    {
        //given
        given(userRepository.findAll()).willReturn(List.of());

        //when
        List<UserDto> result = userService.getAll();

        //then
        then(userRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    @Test
    void shouldGetUserByIdOk() {
        //given
        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.of(user));

        //when
        UserDto result = userService.getById(user.getId());
        //then
        then(userRepository).should(times(1)).findById(user.getId());

        assertEquals(user.getUsername(),result.getUsername());
        assertEquals(user.getLogin(),result.getLogin());
        assertEquals(user.getPassword(),result.getPassword());
        assertEquals(user.getEmail(),result.getEmail());
    }

    @Test
    void shouldGetUserByIdDoesNotExistThrowsException(){

        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> userService.getById(user.getId()));
        then(userRepository).should(times(1)).findById(user.getId());

    }

    @Test
    void shouldAddUserOk() {
        //given
        given(userRepository.save(userArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        UserDto result = userService.add(dto);

        //then
        then(beanValidator).should(times(1)).validate(any());
        then(userRepository).should(times(1)).save(any(User.class));
        assertEquals(user.getUsername(),result.getUsername());
        assertEquals(user.getLogin(),result.getLogin());
        assertEquals(user.getPassword(),result.getPassword());
        assertEquals(user.getEmail(),result.getEmail());

        User captorValue = userArgumentCaptor.getValue();
        assertEquals(user.getUsername(),captorValue.getUsername());
        assertEquals(user.getLogin(),captorValue.getLogin());
        assertEquals(user.getPassword(),captorValue.getPassword());
        assertEquals(user.getEmail(),captorValue.getEmail());

    }

    @Test
    public void shouldAddUserInvalidValuesThrowsException(){
        //given
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());
        //when
        //then
        assertThrows(InvalidBeanException.class, () -> userService.add(dto));
        then(beanValidator).should(times(1)).validate(any());
        then(userRepository).should(times(0)).save(any(User.class));
    }

    @Test
    void shouldUpdateUserOk() {

        //given
        User userToUpdate = new User();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(userToUpdate));
        given(userRepository.save(userArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        UserDto result = userService.update(user.getId(), dto);

        //then
        then(userRepository).should(times(1)).findById(user.getId());
        then(beanValidator).should(times(1)).validate(any());
        assertEquals(user.getUsername(),result.getUsername());
        assertEquals(user.getLogin(),result.getLogin());
        assertEquals(user.getPassword(),result.getPassword());
        assertEquals(user.getEmail(),result.getEmail());

        User captorValue = userArgumentCaptor.getValue();
        assertEquals(user.getUsername(),captorValue.getUsername());
        assertEquals(user.getLogin(),captorValue.getLogin());
        assertEquals(user.getPassword(),captorValue.getPassword());
        assertEquals(user.getEmail(),captorValue.getEmail());
    }

    @Test
    public void shouldUpdateUserInvalidValuesThrowsException()
    {
        //given
        User userToUpdate = new User();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(userToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> userService.update(user.getId(), dto));
        then(userRepository).should(times(1)).findById(user.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(userRepository).should(times(0)).save(any(User.class));
    }

    @Test
    void shouldDeleteUserOk() {
        //given
        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.of(user));

        //when
        String result = userService.delete(user.getId());

        //then
        then(userRepository).should(times(1)).findById(user.getId());
        then(userRepository).should(times(1)).delete(user);
        assertTrue(result.contains("Successfully deleted "));
    }

    @Test
    public void shouldDeleteUserDoesNotExistThrowsException()
    {
        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.empty());

        //when
        String result = userService.delete(user.getId());

        //then
        then(userRepository).should(times(1)).findById(user.getId());
        then(userRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No user"));
    }
}