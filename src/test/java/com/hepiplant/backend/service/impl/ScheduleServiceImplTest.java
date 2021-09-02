package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.repository.ScheduleRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScheduleServiceImplTest {


    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private BeanValidator beanValidator;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Captor
    ArgumentCaptor<Schedule> scheduleArgumentCaptor;

    @Test
    void shouldGetAllOk() {
        //given
        Plant plant = new Plant(1l,"name",null,"location",null,null,null,null,null);
        Schedule schedule = new Schedule(1L, plant,3,21,2);
        given(scheduleRepository.findAll()).willReturn(List.of(schedule));

        //when
        List<ScheduleDto> result = scheduleService.getAll();

        //then
        then(scheduleRepository).should(times(1)).findAll();

        assertEquals(1, result.size());
        assertEquals(3,result.get(0).getWateringFrequency());
        assertEquals(21,result.get(0).getFertilizingFrequency());
        assertEquals(2,result.get(0).getMistingFrequency());
    }

    @Test
    void shouldGetByIdOk() {
        //given
        Plant plant = new Plant(1l,"name",null,"location",null,null,null,null,null);
        Schedule schedule = new Schedule(1L, plant,3,21,2);
        given(scheduleRepository.findById(1L)).willReturn(java.util.Optional.of(schedule));
        //when
        ScheduleDto result = scheduleService.getById(1L);
        //then
        then(scheduleRepository).should(times(1)).findById(1L);

        assertEquals(3,result.getWateringFrequency());
        assertEquals(21,result.getFertilizingFrequency());
        assertEquals(2,result.getMistingFrequency());
    }


    @Test
    void shouldAddOk() {
        //given
        Plant plant = new Plant(1l,"name",null,"location",null,null,null,null,null);
        Schedule schedule = new Schedule(1L, plant,3,21,2);

        //when
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArguments()[0]);

        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setWateringFrequency(schedule.getWateringFrequency());
        scheduleDto.setMistingFrequency(schedule.getMistingFrequency());
        scheduleDto.setFertilizingFrequency(schedule.getFertilizingFrequency());
        ScheduleDto result = scheduleService.add(scheduleDto); //tothink

        //then
        then(scheduleRepository).should(times(1)).save(any(Schedule.class));
        assertEquals(3,result.getWateringFrequency());
        assertEquals(21,result.getFertilizingFrequency());
        assertEquals(2,result.getMistingFrequency());

    }

    @Test
    void shouldUpdateOk() {
//        //given
//        Plant plant = new Plant(1l,"name",null,"location",null,null,null,null,null);
//        Schedule schedule = new Schedule(1L, plant,3,21,2);
//
//        //when
//        when(scheduleRepository.save(schedule)).thenAnswer(i -> i.getArguments()[0]);
//
//        ScheduleDto scheduleDto = new ScheduleDto();
//        scheduleDto.setWateringFrequency(schedule.getWateringFrequency());
//        scheduleDto.setMistingFrequency(schedule.getMistingFrequency());
//        scheduleDto.setFertilizingFrequency(schedule.getFertilizingFrequency());
//        ScheduleDto result = scheduleService.update(1L,scheduleDto); //tothink
//
//        //then
//        then(scheduleRepository).should(times(1)).save(schedule);
//        assertEquals(3,result.getWateringFrequency());
//        assertEquals(21,result.getFertilizingFrequency());
//        assertEquals(2,result.getMistingFrequency());
    }

    @Test
    void shouldDeleteOk() {

    }
}