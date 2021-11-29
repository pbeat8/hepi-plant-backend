package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.entity.Event;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.EventRepository;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private PlantRepository plantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<Event> eventArgumentCaptor;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventDto dto;
    private static Plant plant;
    private static User user;

    @BeforeAll
    public static void initializeVariables(){
        user = new User(1L,"username","uid","email",true,"12:00:00",null,null,null,null);
        plant = new Plant(1L, "Name", null, "location", null, null, null, user, new ArrayList<>(), null);
    }

    @BeforeEach
    public void initializeEvent(){
        event = new Event(1L, "EventName", "EventDescription", null, false, plant);
        dto = new EventDto();
        dto.setId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventDate(event.getEventDate());
        dto.setDone(event.isDone());
        dto.setPlantId(event.getPlant().getId());

    }

    //CREATE tests
    @Test
    public void shouldCreateEvent(){
        //given
        given(plantRepository.findById(dto.getPlantId())).willReturn(Optional.of(plant));
        given(eventRepository.save(eventArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        EventDto result = eventService.add(dto);

        //then
        then(plantRepository).should(times(1)).findById(dto.getPlantId());
        then(beanValidator).should(times(1)).validate(any());
        then(eventRepository).should(times(1)).save(any(Event.class));
        assertEquals(event.getEventName(), result.getEventName());
        assertEquals(event.getEventDescription(), result.getEventDescription());
        assertEquals(event.getEventDate(), result.getEventDate());
        assertEquals(event.isDone(), result.isDone());
        assertEquals(plant.getId(), result.getPlantId());

        Event captorValue = eventArgumentCaptor.getValue();
        assertEquals(event.getEventName(), result.getEventName());
        assertEquals(event.getEventDescription(), result.getEventDescription());
        assertEquals(event.getEventDate(), result.getEventDate());
        assertEquals(event.isDone(), result.isDone());
        assertEquals(plant.getId(), result.getPlantId());
    }

    @Test
    public void shouldCreateEventPlantDoesNotExistThrowsException(){
        //given
        given(plantRepository.findById(dto.getPlantId())).willReturn(Optional.empty());
        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> eventService.add(dto));
        then(plantRepository).should(times(1)).findById(eq(dto.getPlantId()));
        then(eventRepository).should(times(0)).save(any(Event.class));
    }

    @Test
    public void shouldCreateEventInvalidValuesThrowsException(){
        //given
        given(plantRepository.findById(dto.getPlantId())).willReturn(Optional.of(plant));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> eventService.add(dto));
        then(plantRepository).should(times(1)).findById(eq(dto.getPlantId()));
        then(beanValidator).should(times(1)).validate(any());
        then(eventRepository).should(times(0)).save(any(Event.class));
    }

    //GET ALL tests

    @Test
    public void shouldGetAllEventsOk(){
        //given
        given(eventRepository.findAll()).willReturn(List.of(event));

        //when
        List<EventDto> result = eventService.getAll();

        //then
        then(eventRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(event.getEventName(), result.get(0).getEventName());
        assertEquals(event.getEventDescription(), result.get(0).getEventDescription());
        assertEquals(event.getEventDate(), result.get(0).getEventDate());
        assertEquals(event.isDone(), result.get(0).isDone());
        assertEquals(plant.getId(), result.get(0).getPlantId());
    }

    @Test
    public void shouldGetAllEventsEmptyList() {
        //given
        given(eventRepository.findAll()).willReturn(List.of());

        //when
        List<EventDto> result = eventService.getAll();

        //then
        then(eventRepository).should(times(1)).findAll();
        assertEquals(0, result.size());

    }

    // GET BY ID tests
    @Test
    public void shouldGetEventByIdOk() {
        //given
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        //when
        EventDto result = eventService.getById(event.getId());
        //then
        then(eventRepository).should(times(1)).findById(event.getId());
        assertEquals(event.getEventName(), result.getEventName());
        assertEquals(event.getEventDescription(), result.getEventDescription());
        assertEquals(event.getEventDate(), result.getEventDate());
        assertEquals(event.isDone(), result.isDone());
        assertEquals(plant.getId(), result.getPlantId());
    }

    @Test
    public void shouldGetEventByIdEventDoesNotExistThrowsException(){
        //given
        given(eventRepository.findById(event.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> eventService.getById(event.getId()));
        then(eventRepository).should(times(1)).findById(event.getId());
    }

    @Test
    public void shouldGetEventsByPlantOk(){
        event.setDone(true);
        //given
        given(plantRepository.findById(dto.getPlantId())).willReturn(Optional.of(plant));
        given(eventRepository.findAllByPlant(event.getPlant())).willReturn(List.of(event));

        //when
        List<EventDto> result = eventService.getAllByPlant(dto.getPlantId());

        //then
        then(plantRepository).should(times(1)).findById(eq(dto.getPlantId()));
        then(eventRepository).should(times(1)).findAllByPlant(eq(event.getPlant()));
        assertEquals(1, result.size());
        assertEquals(event.getEventName(), result.get(0).getEventName());
        assertEquals(event.getEventDescription(), result.get(0).getEventDescription());
        assertEquals(event.getEventDate(), result.get(0).getEventDate());
        assertEquals(event.isDone(), result.get(0).isDone());
        assertEquals(plant.getId(), result.get(0).getPlantId());
    }

    @Test
    public void shouldGetEventsByPlantEmptyList() {
        //given
        given(plantRepository.findById(dto.getPlantId())).willReturn(Optional.of(plant));
        given(eventRepository.findAllByPlant(event.getPlant())).willReturn(List.of());

        //when
        List<EventDto> result = eventService.getAllByPlant(dto.getPlantId());

        //then
        then(plantRepository).should(times(1)).findById(eq(dto.getPlantId()));
        then(eventRepository).should(times(1)).findAllByPlant(event.getPlant());
        assertEquals(0, result.size());
    }

    @Test
    public void shouldGetEventsByUserOk(){
        user.setPlantList(List.of(plant));
        //given
        given(userRepository.findById(event.getPlant().getUser().getId())).willReturn(Optional.of(user));
        given(eventRepository.findAll()).willReturn(List.of(event));

        //when
        List<EventDto> result = eventService.getAllByUser(event.getPlant().getUser().getId());

        //then
        then(userRepository).should(times(1)).findById(eq(event.getPlant().getUser().getId()));
        then(eventRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(event.getEventName(), result.get(0).getEventName());
        assertEquals(event.getEventDescription(), result.get(0).getEventDescription());
        assertEquals(event.getEventDate(), result.get(0).getEventDate());
        assertEquals(event.isDone(), result.get(0).isDone());
        assertEquals(plant.getId(), result.get(0).getPlantId());
    }

    @Test
    public void shouldGetEventsByUserEmptyList() {
        user.setPlantList(List.of(plant));
        //given
        given(userRepository.findById(event.getPlant().getUser().getId())).willReturn(Optional.of(user));
        given(eventRepository.findAll()).willReturn(List.of());

        //when
        List<EventDto> result = eventService.getAllByUser(event.getPlant().getUser().getId());

        //then
        then(userRepository).should(times(1)).findById(eq(event.getPlant().getUser().getId()));
        then(eventRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    //UPDATE tests
    @Test
    public void shouldUpdateEventOk(){
        //given
        Event eventToUpdate = new Event();
        dto.setPlantId(null);

        given(eventRepository.findById(event.getId())).willReturn(Optional.of(eventToUpdate));
        given(eventRepository.save(eventArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        EventDto result = eventService.update(event.getId(), dto);

        //then
        then(eventRepository).should(times(1)).findById(event.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(eventRepository).should(times(1)).save(any(Event.class));
        assertEquals(event.getEventName(), result.getEventName());
        assertEquals(event.getEventDescription(), result.getEventDescription());
        assertEquals(event.getEventDate(), result.getEventDate());
        assertEquals(event.isDone(), result.isDone());

        Event captorValue = eventArgumentCaptor.getValue();
        assertEquals(event.getEventName(), result.getEventName());
        assertEquals(event.getEventDescription(), result.getEventDescription());
        assertEquals(event.getEventDate(), result.getEventDate());
        assertEquals(event.isDone(), result.isDone());
    }

    @Test
    public void shouldUpdateEventDoesNotExistThrowsException(){
        //given
        dto.setPlantId(null);
        given(eventRepository.findById(event.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> eventService.update(event.getId(), dto));
        then(eventRepository).should(times(1)).findById(event.getId());
        then(eventRepository).should(times(0)).save(any(Event.class));
    }

    @Test
    public void shouldUpdateEventPlantChangeThrowsException(){
        //given
        Event eventToUpdate = new Event();

        given(eventRepository.findById(event.getId())).willReturn(Optional.of(eventToUpdate));

        //when

        //then
        assertThrows(ImmutableFieldException.class, () -> eventService.update(event.getId(), dto));
        then(eventRepository).should(times(1)).findById(event.getId());
        then(eventRepository).should(times(0)).save(any(Event.class));
    }

    @Test
    public void shouldUpdateEventInvalidValuesThrowsException()
    {
        //given
        Event eventToUpdate = new Event();
        dto.setPlantId(null);

        given(eventRepository.findById(event.getId())).willReturn(Optional.of(eventToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> eventService.update(event.getId(), dto));
        then(eventRepository).should(times(1)).findById(event.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(eventRepository).should(times(0)).save(any(Event.class));
    }

    //DELETE tests
    @Test
    public void shouldDeleteEventOk() {
        //given
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        //when
        String result = eventService.delete(event.getId());

        //then
        then(eventRepository).should(times(1)).findById(event.getId());
        then(eventRepository).should(times(1)).delete(event);
        assertTrue(result.contains("Successfully deleted"));

    }

    @Test
    public void shouldDeleteEventDoesNotExistThrowsException() {
        //given
        given(eventRepository.findById(event.getId())).willReturn(Optional.empty());

        //when
        String result = eventService.delete(event.getId());

        //then
        then(eventRepository).should(times(1)).findById(event.getId());
        then(eventRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No event"));
    }

}
