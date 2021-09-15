package com.crm.api.v1;

import com.crm.dto.request.CarRequest;
import com.crm.dto.request.PageRequest;
import com.crm.dto.response.CarResponse;
import com.crm.model.db.CarEntity;
import com.crm.service.impl.CarServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarServiceImpl carService;

    @GetMapping
    @ApiOperation(value = "Finds all cars paginated", notes = "Add param \"page\" and/or \"size\""
            + " to specify page no. and size of each page.")
    public ResponseEntity<Page<CarResponse>> getCarsPaginated(@Valid final PageRequest pageRequest) {
        return ResponseEntity.ok(carService.getCarsPaginated(pageRequest.getPage(), pageRequest.getSize()));
    }

    @GetMapping("/{carId}")
    @ApiOperation(value = "Endpoint enable getting car by Id", notes = "Add param \"carId\" to specify which car should be returned")
    public ResponseEntity<CarResponse> getCarById(@PathVariable final Long carId) {
        return ResponseEntity.ok(carService.getCarById(carId));
    }

    @GetMapping("/search")
    @ApiOperation(value = "Finds car by registration number", notes = "Add param \"license-plate\""
            + " to specify which car should be returned given its license plate number")
    public ResponseEntity<CarResponse> getCarByRegistrationNumber(@RequestParam(name = "license-plate") final String registrationNumber) {
        return ResponseEntity.ok(carService.getCarByRegistrationNumber(registrationNumber));
    }

    @PostMapping
    @ApiOperation(value = "Add new car", notes = "Add body \"CarRequest\" to add new car to database")
    public ResponseEntity<?> addCar(@Valid @RequestBody final CarRequest carRequest) {
        CarEntity car = carService.addCar(carRequest);

        return ResponseEntity.created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + car.getId().toString())
                        .build()
                        .toUri())
                .build();
    }
}
