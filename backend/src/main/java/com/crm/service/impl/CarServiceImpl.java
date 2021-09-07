package com.crm.service.impl;

import com.crm.dto.mapper.CarMapper;
import com.crm.dto.response.CarResponse;
import com.crm.exception.CarNotFoundException;
import com.crm.exception.ErrorDict;
import com.crm.model.db.CarEntity;
import com.crm.repository.CarRepository;
import com.crm.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    @Value("${crm.properties.vin.length}")
    private int VIN_LENGTH;

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponse> getCarsPaginated(final int page, final int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarEntity> carsPage = carRepository.findAll(pageable);

        final List<CarResponse> carResponseList = carsPage.getContent()
                .stream()
                .map(carMapper::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(carResponseList, pageable, carsPage.getTotalPages());
    }

    @Override
    public CarResponse getCarById(final Long id) {
        return carMapper.convertToDto(carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(ErrorDict.CAR_NOT_FOUND)));
    }

    @Override
    public CarResponse getCarByRegistrationNumber(final String registrationNumber) {
        return carRepository.findByRegistrationNumberIgnoreCase(registrationNumber)
                .map(carMapper::convertToDto)
                .orElseThrow(() -> new CarNotFoundException(ErrorDict.REGISTRATION_NUMBER_NOT_FOUND));
    }

    public CarResponse getCarByVIN(String vin) {
        if (vin.length() != VIN_LENGTH)
            throw new CarNotFoundException(ErrorDict.VIN_INVALID_LENGTH);

        if (hasVinIllegalCharacters(vin))
            throw new CarNotFoundException(ErrorDict.VIN_ILLEGAL_CHARACTERS);

        return carRepository.findByVinIgnoreCase(vin)
                .map(carMapper::convertToDto)
                .orElseThrow(() -> new CarNotFoundException(ErrorDict.VIN_NOT_FOUND));
    }

    private boolean hasVinIllegalCharacters(String vin) {
        return vin.toLowerCase().contains("o") || vin.toLowerCase().contains("i") || vin.toLowerCase().contains("q");
    }
}
