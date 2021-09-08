package com.crm.service.impl;

import com.crm.dto.mapper.CarMapper;
import com.crm.dto.request.CarRequest;
import com.crm.dto.response.CarResponse;
import com.crm.exception.CarHandlingException;
import com.crm.exception.CarNotFoundException;
import com.crm.exception.ErrorDict;
import com.crm.model.db.CarEntity;
import com.crm.model.db.CarTypeEntity;
import com.crm.repository.CarRepository;
import com.crm.repository.CarTypeRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class CarServiceImplTest {

    private static final Long ONE = 1L;
    private static final String REGISTRATION_NUMBER = "ABC123";

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarTypeRepository carTypeRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private static CarEntity carEntity;
    private static CarResponse carResponse;
    private static CarRequest carRequest;

    @BeforeClass
    public static void setUp() {
        carEntity = new CarEntity();
        carResponse = new CarResponse();
        carRequest = new CarRequest();
    }

    @Test
    public void getCarByIdShouldReturnCorrectResponse() {
        final Optional<CarEntity> optionalCarEntity = Optional.of(CarServiceImplTest.carEntity);
        when(carRepository.findById(ONE)).thenReturn(optionalCarEntity);
        when(carMapper.convertToDto(carEntity)).thenReturn(carResponse);

        carService.getCarById(ONE);

        verify(carRepository, times(1)).findById(ONE);
        verify(carMapper, times(1)).convertToDto(carEntity);
    }

    @Test(expected = CarNotFoundException.class)
    public void getCarByIdShouldThrowExceptionForNotExistingEntry() {
        when(carRepository.findById(ONE)).thenReturn(Optional.empty());

        carService.getCarById(ONE);

        verify(carRepository, times(1)).findById(ONE);
        verify(carMapper, times(0)).convertToDto(any());
    }

    @Test
    public void getCarByRegistrationNumberShouldReturnCorrectResponse() {
        when(carRepository.findByRegistrationNumberIgnoreCase(REGISTRATION_NUMBER)).thenReturn(Optional.of(carEntity));
        when(carMapper.convertToDto(carEntity)).thenReturn(carResponse);

        final CarResponse response = carService.getCarByRegistrationNumber(REGISTRATION_NUMBER);

        verify(carRepository, times(1)).findByRegistrationNumberIgnoreCase(REGISTRATION_NUMBER);
        verify(carMapper, times(1)).convertToDto(any());
        assertNotNull(response);
    }

    @Test(expected = CarNotFoundException.class)
    public void getCarByRegistrationNumberShouldThrowExceptionForInvalidRegistrationNumber() {
        when(carRepository.findByRegistrationNumberIgnoreCase(REGISTRATION_NUMBER)).thenReturn(Optional.empty());

        carService.getCarByRegistrationNumber(REGISTRATION_NUMBER);

        verify(carRepository, times(1)).findByRegistrationNumberIgnoreCase(REGISTRATION_NUMBER);
        verify(carMapper, times(0)).convertToDto(any());
    }

    @Test
    public void shouldThrowConflictWhenVinExists() {
        final String existingVin = "VWVWVW12345678901";
        carRequest = CarRequest.builder()
                .vin(existingVin)
                .carTypeEntity(CarTypeEntity.builder().name("").build())
                .build();

        carEntity = CarEntity.builder().vin(existingVin).build();

        when(carRepository.findByVinIgnoreCase(existingVin)).thenReturn(Optional.of(carEntity));

        assertThatThrownBy(() -> carService.addCar(carRequest))
                .isInstanceOf(CarHandlingException.class)
                .hasMessage(ErrorDict.CAR_CREATE_VIN_EXISTS);

        verify(carRepository, times(1)).findByVinIgnoreCase(existingVin);
    }

    @Test
    public void shouldThrowConflictWhenRegistrationNumberExists() {
        final String existingRegistrationNumber = "KR12345";
        carRequest = CarRequest.builder()
                .registrationNumber(existingRegistrationNumber)
                .carTypeEntity(CarTypeEntity.builder().name("").build())
                .build();

        carEntity = CarEntity.builder().registrationNumber(existingRegistrationNumber).build();

        when(carRepository.findByRegistrationNumberIgnoreCase(existingRegistrationNumber))
                .thenReturn(Optional.of(carEntity));

        assertThatThrownBy(() -> carService.addCar(carRequest))
                .isInstanceOf(CarHandlingException.class)
                .hasMessage(ErrorDict.CAR_CREATE_REGISTRATION_NUMBER_EXISTS);

        verify(carRepository, times(1)).findByRegistrationNumberIgnoreCase(existingRegistrationNumber);
    }

    @Test
    public void shouldAddCarWithTypeOtherWhenCarTypeNotProvided() {
        carRequest = CarRequest.builder()
                .carTypeEntity(CarTypeEntity.builder().name("").build())
                .build();

        when(carTypeRepository.findByNameIgnoreCase("Other"))
                .thenReturn(Optional.of(CarTypeEntity.builder().name("Other").build()));

        when(carMapper.convertToEntity(carRequest)).thenReturn(carEntity);

        when(carRepository.save(carEntity)).thenReturn(carEntity);

        CarEntity addedCar = carService.addCar(carRequest);

        assertEquals("Other", addedCar.getCarTypeEntity().getName());

        verify(carTypeRepository, times(1)).findByNameIgnoreCase("Other");
        verify(carMapper, times(1)).convertToEntity(carRequest);
        verify(carRepository, times(1)).save(carEntity);
    }

    @Test
    public void shouldAddCarWhenCarTypeProvided() {
        final String carTypeName = "Sedan";

        carRequest = CarRequest.builder()
                .carTypeEntity(CarTypeEntity.builder().name(carTypeName).build())
                .build();

        when(carTypeRepository.findByNameIgnoreCase(carTypeName))
                .thenReturn(Optional.of(CarTypeEntity.builder().name(carTypeName).build()));

        when(carMapper.convertToEntity(carRequest)).thenReturn(carEntity);

        when(carRepository.save(carEntity)).thenReturn(carEntity);

        CarEntity addedCar = carService.addCar(carRequest);

        assertEquals(carTypeName, addedCar.getCarTypeEntity().getName());

        verify(carTypeRepository, times(1)).findByNameIgnoreCase(carTypeName);
        verify(carMapper, times(1)).convertToEntity(carRequest);
        verify(carRepository, times(1)).save(carEntity);
    }
}
