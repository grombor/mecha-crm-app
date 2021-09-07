package com.crm.api.v1;

import com.crm.App;
import com.crm.BaseIntegrationTest;
import com.crm.dto.response.CarResponse;
import com.crm.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@WithMockUser
class CarControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCorrectResponseStatusWhenCallingCarsEndpoint() throws Exception {
        mvc.perform(get("/cars"))
                .andExpect(status().is(OK.value()));
    }

    @Test
    void shouldResponseEntityHasPageNumberEqualTo2WhenParamSizeEqualTo1() throws Exception {
        mvc.perform(get("/cars?page=1&size=1"))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.pageable.pageNumber", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(1)));
    }

    @Test
    void shouldResponseWithDefaultValues() throws Exception {
        mvc.perform(get("/cars"))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(20)));
    }

    @Test
    void shouldReturnCorrectResponseStatusWhenCallingCarWithExistingId_WhenDescriptionNotBlank() throws Exception {
        mvc.perform(get("/cars/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.vin", is("JT3Z123KBW1589043")))
                .andExpect(jsonPath("$.registrationNumber", is("KR12PR")))
                .andExpect(jsonPath("$.brand", is("Toyota")))
                .andExpect(jsonPath("$.model", is("Avensis")))
                .andExpect(jsonPath("$.productionYear", is(2014)))
                .andExpect(jsonPath("$.mileage", is(123456)))
                .andExpect(jsonPath("$.description", is("Some description")))
                .andExpect(jsonPath("$.carTypeEntity.id", is(3)))
                .andExpect(jsonPath("$.carTypeEntity.name", is("Combi")))
                .andExpect(status().is(OK.value()));
    }

    @Test
    void shouldReturnCorrectResponseStatusWhenCallingCarWithExistingId_WhenDescriptionIsBlank() throws Exception {
        mvc.perform(get("/cars/4"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.vin", is("WBA86H90021859321")))
                .andExpect(jsonPath("$.registrationNumber", is("ELAS23")))
                .andExpect(jsonPath("$.brand", is("BMW")))
                .andExpect(jsonPath("$.model", is("M5")))
                .andExpect(jsonPath("$.productionYear", is(2020)))
                .andExpect(jsonPath("$.mileage", is(22500)))
                .andExpect(jsonPath("$.description", is("")))
                .andExpect(jsonPath("$.carTypeEntity.id", is(1)))
                .andExpect(jsonPath("$.carTypeEntity.name", is("Sedan")))
                .andExpect(status().is(OK.value()));
    }

    @Test
    void shouldReturnCorrectResponseStatusWhenCallingCarWithExistingId_WhenDescriptionIsNull() throws Exception {
        mvc.perform(get("/cars/6"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.vin", is("TMB1234FGH5678910")))
                .andExpect(jsonPath("$.registrationNumber", is("SK123F")))
                .andExpect(jsonPath("$.brand", is("Skoda")))
                .andExpect(jsonPath("$.model", is("Octavia")))
                .andExpect(jsonPath("$.productionYear", is(2010)))
                .andExpect(jsonPath("$.mileage", is(260000)))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.carTypeEntity.id", is(1)))
                .andExpect(jsonPath("$.carTypeEntity.name", is("Sedan")))
                .andExpect(status().is(OK.value()));
    }

    @Test
    void shouldReturnBadResponseStatusWhenCallingCarWithNonExistingId() throws Exception {
        mvc.perform(get("/cars/1000"))
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    void shouldReturnCorrectResponseStatusWhenCallingCarWithValidRegistrationNumber() throws Exception {
        mvc.perform(get("/cars/search?license-plate=kR12pr"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.vin", is("JT3Z123KBW1589043")))
                .andExpect(jsonPath("$.registrationNumber", is("KR12PR")))
                .andExpect(jsonPath("$.brand", is("Toyota")))
                .andExpect(jsonPath("$.model", is("Avensis")))
                .andExpect(jsonPath("$.productionYear", is(2014)))
                .andExpect(jsonPath("$.mileage", is(123456)))
                .andExpect(jsonPath("$.description", is("Some description")))
                .andExpect(jsonPath("$.carTypeEntity.id", is(3)))
                .andExpect(jsonPath("$.carTypeEntity.name", is("Combi")))
                .andExpect(status().is(OK.value()));
    }

    @Test
    void shouldReturnBadResponseStatusWhenCallingCarWithInvalidRegistrationNumber() throws Exception {
        mvc.perform(get("/cars/search?license-plate=KR"))
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    void shouldReturnBadResponseStatusWhenCallingCarWithBlankRegistrationNumber() throws Exception {
        mvc.perform(get("/cars/search?license-plate="))
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @SneakyThrows
    @Test
    void shouldResponseWith_NotFound_WhenVinIsInvalid() {
        final String expectedMessages = "Błędna długość numeru VIN, wymagane jest 17 znaków";

        MvcResult mvcResult = mvc.perform(get("/cars/search?vin=vvvvv"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult
                .getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(expectedMessages));
        assertEquals(1, errorResponse.getMessage().size());
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_NotFound_WhenVinHasInvalidCharacters() {
        final String expectedMessage = "VIN nie może zawierać liter O(o), I(i) and Q(q)";
        final String vinWithInvalidCharacters = "vavaaav123456781q";

        MvcResult mvcResult = mvc.perform(get("/cars/search?vin=" + vinWithInvalidCharacters))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult
                .getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(expectedMessage));
        assertEquals(1, errorResponse.getMessage().size());
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_NotFound_WhenVinDoesNotExists() {
        final String expectedMessage = "Podany numer VIN nie istnieje";
        final String vinDoesNotExists = "a1w2e3r4t5y6u7g8b";

        MvcResult mvcResult = mvc.perform(get("/cars/search?vin=" + vinDoesNotExists))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult
                .getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(expectedMessage));
        assertEquals(1, errorResponse.getMessage().size());
    }

    @Test
    @SneakyThrows
    void shouldResponseOk_WhenVinExists() {
        final String vinDoesExists = "TMB67890123456452";

        MvcResult mvcResult = mvc.perform(get("/cars/search?vin=" + vinDoesExists))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        CarResponse carResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CarResponse.class);

        assertEquals(carResponse.getVin(), vinDoesExists);
    }
}
