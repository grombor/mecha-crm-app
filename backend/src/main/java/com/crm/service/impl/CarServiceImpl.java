package com.crm.service.impl;

import com.crm.dto.mapper.CarMapper;
import com.crm.dto.request.CarRequest;
import com.crm.dto.response.CarResponse;
import com.crm.model.db.CarEntity;
import com.crm.repository.CarRepository;
import com.crm.service.CarService;
import lombok.RequiredArgsConstructor;
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
    public void addCar(CarRequest carRequest) {
        carRepository.save(carMapper.convertToEntity(carRequest));
    }
}
