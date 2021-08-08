package com.crm.api.v1;

import com.crm.App;
import com.crm.dto.request.CarRequest;
import com.crm.model.db.CarTypeEntity;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@WithMockUser
public class CarControllerIT {

    @Autowired
    private MockMvc mvc;

    private static final String VIN = "JT3Z123KBW1234567";
    private static final String REGISTRATION_NUMBER = "TE12ST";
    private static final String BRAND = "Toyota";
    private static final String MODEL = "Avensis";
    private static final Integer PRODUCTION_YEAR = 2016;
    private static final Integer MILEAGE = 50000;
    private static final String DESCRIPTION = "TEST";
    private static final CarTypeEntity CAR_TYPE_ENTITY = new CarTypeEntity(4L, "VAN");
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    @Test
    public void shouldReturnCorrectResponseStatusWhenCallingCarsEndpoint() throws Exception {
        mvc.perform(get("/cars"))
                .andExpect(status().is(OK.value()));
    }

    @Test
    public void shouldResponseEntityHasPageNumberEqualTo2WhenParamSizeEqualTo1() throws Exception {
        mvc.perform(get("/cars?page=1&size=1"))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.pageable.pageNumber", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(1)));
    }

    @Test
    public void shouldResponseWithDefaultValues() throws Exception {
        mvc.perform(get("/cars"))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(20)));
    }

    @Test
    public void shouldReturnCorrectResponseStatusWhenCallingCarWithExistingId() throws Exception {
        mvc.perform(get("/cars/1"))
                .andExpect(status().is(OK.value()));
    }

    @Test
    public void shouldReturnBadResponseStatusWhenCallingCarWithNonExistingId() throws Exception {
        mvc.perform(get("/cars/1000"))
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    public void shouldReturnCorrectResponseStatusWhenInsertingCar() throws Exception {
        var carRequest = new CarRequest();
        carRequest.setVin(VIN);
        carRequest.setRegistrationNumber(REGISTRATION_NUMBER);
        carRequest.setBrand(BRAND);
        carRequest.setModel(MODEL);
        carRequest.setProductionYear(PRODUCTION_YEAR);
        carRequest.setMileage(MILEAGE);
        carRequest.setDescription(DESCRIPTION);
        carRequest.setCarTypeEntity(CAR_TYPE_ENTITY);
        var gson = new Gson();
        String json = gson.toJson(carRequest);

        mvc.perform(post("/cars").contentType(APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isCreated());
    }


}