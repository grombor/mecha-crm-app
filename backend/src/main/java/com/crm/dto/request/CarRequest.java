package com.crm.dto.request;

import com.crm.model.db.CarTypeEntity;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CarRequest {

    @NotEmpty
    private String vin;

    @NotEmpty
    private String registrationNumber;

    @NotEmpty
    private String brand;

    @NotEmpty
    private String model;

    @NotNull
    private Integer productionYear;

    @NotNull
    private Integer mileage;

    private String description;

    @NotNull
    private CarTypeEntity carTypeEntity;
}
