package com.crm.api.v1;

import com.crm.App;
import com.crm.BaseIntegrationTest;
import com.crm.dto.request.CarRequest;
import com.crm.exception.ErrorDict;
import com.crm.exception.ErrorResponse;
import com.crm.model.db.CarTypeEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@WithMockUser
class CarControllerIT extends BaseIntegrationTest {

    private CarRequest carRequest;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        carRequest = new CarRequest();

        carRequest = CarRequest.builder()
                .id(30L)
                .vin("azwsxedc123456789")
                .registrationNumber("KR12345")
                .brand("Opel")
                .model("Astra")
                .productionYear(1951)
                .mileage(1)
                .description("")
                .carTypeEntity(CarTypeEntity.builder().name("Sedan").build())
                .build();
    }

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

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenBodyIsMissing() {
        mvc.perform(post("/cars"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenBodyIsEmpty() {
        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult
                .getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        List<String> message = errorResponse.getMessage();

        assertTrue(message.contains(ErrorDict.CAR_VIN_INVALID));
        assertTrue(message.contains(ErrorDict.CAR_BRAND_INVALID));
        assertTrue(message.contains(ErrorDict.CAR_MODEL_INVALID));
        assertTrue(message.contains(ErrorDict.CAR_PRODUCTION_YEAR_INVALID));
        assertTrue(message.contains(ErrorDict.CAR_MILEAGE_INVALID));
        assertTrue(message.contains(ErrorDict.CAR_TYPE_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenVinIsBlank() {
        carRequest.setVin("");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_VIN_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenVinDoesNotMatchToPattern() {
        carRequest.setVin("VFTOQ123456789654");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.VIN_FORMAT_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenVinLengthDoesNotMatch() {
        carRequest.setVin("V6789654");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_VIN_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenRegistrationNumberIsShorterThenThreeChars() {
        carRequest.setRegistrationNumber("KR");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.REGISTRATION_NUMBER_LENGTH_MUST_BETWEEN));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenRegistrationNumberIsLongerThenTenChars() {
        carRequest.setRegistrationNumber("KR123456789");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.REGISTRATION_NUMBER_LENGTH_MUST_BETWEEN));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenRegistrationNumberDoesNotMatchToPatern() {
        carRequest.setRegistrationNumber("KR@1234");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.REGISTRATION_NUMBER_FORMAT_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenCarBrandIsBlank() {
        carRequest.setBrand("");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_BRAND_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenCarBrandIsShorterThanThreeChars() {
        carRequest.setBrand("Se");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.BRAND_LENGTH_MUST_BETWEEN));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenCarBrandIsLongerThanThirtyChars() {
        carRequest.setBrand("SedanSedanSedanSedanSedanSedanS");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.BRAND_LENGTH_MUST_BETWEEN));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenCarModelIsBlank() {
        carRequest.setModel("");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_MODEL_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenCarModelIsLongerThanThirtyChars() {
        carRequest.setModel("AstraAstraAstraAstraAstraAstraA");
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.MODEL_LENGTH_NOT_GREATER_THAN));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenProductionYearIsBlank() {
        carRequest.setProductionYear(null);
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_PRODUCTION_YEAR_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenProductionYearIsBefore1950() {
        carRequest.setProductionYear(1949);
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_PRODUCTION_YEAR_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenMileageIsBlank() {
        carRequest.setMileage(null);
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_MILEAGE_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenMileageIsNegative() {
        carRequest.setMileage(-1);
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_MILEAGE_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenDescriptionIsLongerThan250Chars() {
        carRequest.setDescription("a".repeat(251));
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.DESCRIPTION_LENGTH_NOT_GREATER_THAN));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_BedRequest_WhenCarTypeIsNull() {
        carRequest.setCarTypeEntity(null);
        String body = objectMapper.writeValueAsString(carRequest);

        MvcResult mvcResult = mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponse.class);

        assertTrue(errorResponse.getMessage().contains(ErrorDict.CAR_TYPE_INVALID));
    }

    @Test
    @SneakyThrows
    void shouldResponseWith_Created_WhenAddedCar() {
        String body = objectMapper.writeValueAsString(carRequest);

        mvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
