package com.crm.dto.mapper;

import com.crm.dto.request.CarRequest;
import com.crm.dto.response.CarResponse;
import com.crm.model.db.CarEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public CarMapper(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CarResponse convertToDto(final CarEntity carEntity) {
        return modelMapper.map(carEntity, CarResponse.class);
    }

    public CarEntity convertToEntity(final CarResponse carResponse) {
        return modelMapper.map(carResponse, CarEntity.class);
    }

    public CarEntity convertToEntity(final CarRequest carRequest) {
        return modelMapper.map(carRequest, CarEntity.class);
    }
}
