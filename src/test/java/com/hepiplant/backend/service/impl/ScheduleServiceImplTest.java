package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.repository.ScheduleRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private BeanValidator beanValidator;

    @Captor
    ArgumentCaptor<Schedule> scheduleArgumentCaptor;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;
    private Schedule schedule;
    private ScheduleDto dto;
    private static Plant plant;

    @BeforeAll
    public static void initializeVariables(){
        plant = new Plant(1l,"name",LocalDateTime.now(),"location", "photo", null,null,null,null,null);

    }

    @BeforeEach
    public void initializeSchedule(){

        schedule = new Schedule(1L, plant,3,21,2);
        dto = new ScheduleDto();
        dto.setId(schedule.getId());
        dto.setPlantId(schedule.getPlant().getId());
        dto.setWateringFrequency(schedule.getWateringFrequency());
        dto.setMistingFrequency(schedule.getMistingFrequency());
        dto.setFertilizingFrequency(schedule.getFertilizingFrequency());
    }

    @Test
    void shouldGetAllSchedulesOk() {
        //given
        given(scheduleRepository.findAll()).willReturn(List.of(schedule));

        //when
        List<ScheduleDto> result = scheduleService.getAll();

        //then
        then(scheduleRepository).should(times(1)).findAll();

        assertEquals(1, result.size());
        assertEquals(schedule.getWateringFrequency(),result.get(0).getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),result.get(0).getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),result.get(0).getMistingFrequency());
    }

    @Test
    void shouldGetAllSchedulesEmptyListOk() {
        //given
        given(scheduleRepository.findAll()).willReturn(List.of());

        //when
        List<ScheduleDto> result = scheduleService.getAll();

        //then
        then(scheduleRepository).should(times(1)).findAll();

        assertEquals(0, result.size());

    }

    @Test
    void shouldGetAllSchedulesByPlantOk() {
        //given
        given(plantRepository.findById(schedule.getPlant().getId())).willReturn(Optional.of(plant));
        given(scheduleRepository.findAllByPlant(schedule.getPlant())).willReturn(List.of(schedule));

        //when
        List<ScheduleDto> result = scheduleService.getAllByPlant(schedule.getPlant().getId());

        //then
        then(plantRepository).should(times(1)).findById(dto.getPlantId());
        then(scheduleRepository).should(times(1)).findAllByPlant(any(Plant.class));

        assertEquals(1, result.size());
        assertEquals(schedule.getPlant().getId(), result.get(0).getPlantId());
        assertEquals(schedule.getWateringFrequency(),result.get(0).getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),result.get(0).getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),result.get(0).getMistingFrequency());
    }

    @Test
    void shouldGetAllSchedulesByPlantEmptyListOk() {
        //given
        given(plantRepository.findById(anyLong())).willReturn(Optional.of(plant));
        given(scheduleRepository.findAllByPlant(schedule.getPlant())).willReturn(List.of());

        //when
        List<ScheduleDto> result = scheduleService.getAllByPlant(anyLong());

        //then
        then(plantRepository).should(times(1)).findById(anyLong());
        then(scheduleRepository).should(times(1)).findAllByPlant(any(Plant.class));

        assertEquals(0, result.size());
    }

    @Test
    void shouldGetAllSchedulesByPlantDoesNotExistsThrowsException() {
        //given
        given(plantRepository.findById(anyLong())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> scheduleService.getAllByPlant(anyLong()));
        then(plantRepository).should(times(1)).findById(anyLong());
        then(scheduleRepository).should(times(0)).findAllByPlant(any(Plant.class));
    }

    @Test
    void shouldGetScheduleByIdOk() {
        //given
        given(scheduleRepository.findById(schedule.getId())).willReturn(Optional.of(schedule));

        //when
        ScheduleDto result = scheduleService.getById(schedule.getId());

        //then
        then(scheduleRepository).should(times(1)).findById(schedule.getId());

        assertEquals(schedule.getWateringFrequency(),result.getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),result.getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),result.getMistingFrequency());
    }

    @Test
    void shouldGetScheduleByIdNotExistThrowsException(){

        //given
        given(scheduleRepository.findById(schedule.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> scheduleService.getById(schedule.getId()));
        then(scheduleRepository).should(times(1)).findById(schedule.getId());

    }

    @Test
    void shouldAddScheduleOk() {
        //given
        given(plantRepository.findById(schedule.getPlant().getId())).willReturn(Optional.of(plant));
        given(scheduleRepository.save(scheduleArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        System.out.println(dto.getPlantId());
        ScheduleDto result = scheduleService.add(dto);

        //then
        then(plantRepository).should(times(1)).findById(dto.getPlantId());
        then(beanValidator).should(times(1)).validate(any());
        then(scheduleRepository).should(times(1)).save(any(Schedule.class));
        assertEquals(schedule.getPlant().getId(), result.getPlantId());
        assertEquals(schedule.getWateringFrequency(),result.getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),result.getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),result.getMistingFrequency());

        Schedule captorValue = scheduleArgumentCaptor.getValue();
        assertEquals(schedule.getPlant(),captorValue.getPlant());
        assertEquals(schedule.getWateringFrequency(),captorValue.getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),captorValue.getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),captorValue.getMistingFrequency());

    }

    @Test
    void shouldAddScheduleInvalidValuesThrowException() {
        //given
        dto.setFertilizingFrequency(-1);
        dto.setMistingFrequency(-3);
        dto.setWateringFrequency(0);
        given(plantRepository.findById(schedule.getPlant().getId())).willReturn(Optional.of(plant));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(scheduleArgumentCaptor.capture());

        //when
        //then
        assertThrows(InvalidBeanException.class, () -> scheduleService.add(dto));
        Schedule captorValue = scheduleArgumentCaptor.getValue();
        then(plantRepository).should(times(1)).findById(schedule.getPlant().getId());
        then(beanValidator).should(times(1)).validate(any());
        then(scheduleRepository).should(times(0)).save(any(Schedule.class));
        assertEquals(dto.getFertilizingFrequency(), captorValue.getFertilizingFrequency());
        assertEquals(dto.getMistingFrequency(), captorValue.getMistingFrequency());
        assertEquals(dto.getWateringFrequency(), captorValue.getWateringFrequency());

    }

    @Test
    void shouldAddScheduleInvalidPlantThrowException() {
        //given
        given(plantRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> scheduleService.add(dto));
        then(plantRepository).should(times(1)).findById(anyLong());
        then(scheduleRepository).should(times(0)).findAllByPlant(any(Plant.class));
    }

    @Test
    void shouldUpdateScheduleOk() {
        //given
        Schedule scheduleToUpdate = new Schedule();
        dto.setPlantId(null);
        given(scheduleRepository.findById(schedule.getId())).willReturn(java.util.Optional.of(scheduleToUpdate));
        given(scheduleRepository.save(scheduleArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        ScheduleDto result = scheduleService.update(schedule.getId(),dto);

        //then
        then(scheduleRepository).should(times(1)).findById(schedule.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(scheduleRepository).should(times(1)).save(any(Schedule.class));

        assertEquals(schedule.getWateringFrequency(),result.getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),result.getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),result.getMistingFrequency());

        Schedule captorValue = scheduleArgumentCaptor.getValue();
        assertEquals(schedule.getWateringFrequency(),captorValue.getWateringFrequency());
        assertEquals(schedule.getFertilizingFrequency(),captorValue.getFertilizingFrequency());
        assertEquals(schedule.getMistingFrequency(),captorValue.getMistingFrequency());
    }

    @Test
    public void shouldUpdateScheduleInvalidValuesThrowsException()
    {
        //given
        Schedule scheduleToUpdate = new Schedule();
        dto.setPlantId(null);
        given(scheduleRepository.findById(schedule.getId())).willReturn(java.util.Optional.of(scheduleToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when
        //then
        assertThrows(InvalidBeanException.class, () -> scheduleService.update(schedule.getId(), dto));
        then(scheduleRepository).should(times(1)).findById(schedule.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(scheduleRepository).should(times(0)).save(any(Schedule.class));
    }

    @Test
    public void shouldUpdateScheduleInvalidPlantChangeThrowsException()
    {
        //given
        Schedule scheduleToUpdate = new Schedule();
        given(scheduleRepository.findById(schedule.getId())).willReturn(Optional.of(scheduleToUpdate));

        //when
        //then
        assertThrows(ImmutableFieldException.class, () -> scheduleService.update(schedule.getId(), dto));
        then(scheduleRepository).should(times(1)).findById(schedule.getId());
        then(plantRepository).should(atMostOnce()).findById(eq(dto.getPlantId()));
        then(scheduleRepository).should(times(0)).save(any(Schedule.class));
    }

    @Test
    public void shouldUpdateScheduleThatNotExistsThrowsException()
    {
        //given
        given(scheduleRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> scheduleService.update(schedule.getId(), dto));
        then(scheduleRepository).should(times(1)).findById(schedule.getId());
        then(scheduleRepository).should(times(0)).save(any(Schedule.class));
    }

    @Test
    void shouldDeleteScheduleOk() {
        //given
        given(scheduleRepository.findById(schedule.getId())).willReturn(java.util.Optional.of(schedule));

        //when
        String result = scheduleService.delete(schedule.getId());

        //then
        then(scheduleRepository).should(times(1)).findById(schedule.getId());
        then(scheduleRepository).should(times(1)).delete(schedule);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeleteScheduleDoesNotExistThrowsException()
    {
        //given
        given(scheduleRepository.findById(schedule.getId())).willReturn(Optional.empty());

        //when
        String result = scheduleService.delete(schedule.getId());

        //then
        then(scheduleRepository).should(times(1)).findById(schedule.getId());
        then(scheduleRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No schedule"));
    }

}
