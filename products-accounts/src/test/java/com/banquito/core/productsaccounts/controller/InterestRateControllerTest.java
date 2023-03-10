package com.banquito.core.productsaccounts.controller;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.banquito.core.productsaccounts.controller.dto.InterestRateRQRS;
import com.banquito.core.productsaccounts.controller.mapper.InterestRateMapper;
import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.InterestRate;
import com.banquito.core.productsaccounts.service.InterestRateService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class InterestRateControllerTest {

    @Mock
    private InterestRateService service;

    @InjectMocks
    private InterestRateController controller;


    @Test
    public void testObtainAllInterestRates() {
        // define the behavior of the mock object
        List<InterestRate> interestRates = new ArrayList<>();
        InterestRate interestRate = new InterestRate();
//        "1", "Rate 1", 0.05, true
        interestRate.setId(1);
        interestRate.setName("Rate 1");
        interestRate.setInterestRate(BigDecimal.valueOf(0.05));
        interestRate.setState("ACT");
        interestRates.add(interestRate);
        InterestRate interestRate2 = new InterestRate();
//        "2", "Rate 2", 0.10, true
        interestRate2.setId(2);
        interestRate2.setName("Rate 2");
        interestRate2.setInterestRate(BigDecimal.valueOf(0.10));
        interestRate2.setState("ACT");

        interestRates.add(interestRate2);
        when(service.listAllActives()).thenReturn(interestRates);

        // call the obtainAll method
        ResponseEntity<List<InterestRateRQRS>> responseEntity = controller.obtainAll();

        // verify that the correct list of interest rates was returned
        List<InterestRateRQRS> responseList = responseEntity.getBody();
        Assertions.assertEquals(interestRates.size(), responseList.size());
        for (int i = 0; i < interestRates.size(); i++) {
            InterestRateRQRS expected = InterestRateMapper.mapToInterestRateRQRS(interestRates.get(i));
            InterestRateRQRS actual = responseList.get(i);
            Assertions.assertEquals(expected.getId(), actual.getId());
            Assertions.assertEquals(expected.getName(), actual.getName());
            Assertions.assertEquals(expected.getInterestRate(), actual.getInterestRate());
        }
    }

    @Test
    public void testObtainByCode() {
        // Create a mock InterestRate object
        InterestRate interestRate = new InterestRate();
        interestRate.setId(1);
        interestRate.setInterestRate(BigDecimal.valueOf(0.05));

        // Set up the mock service to return the mock InterestRate when obtainById() is called
        when(service.obtainById(anyInt())).thenReturn(interestRate);

        // Call the obtainByCode() method with a mock id parameter
        ResponseEntity<InterestRateRQRS> response = controller.obtainByCode("1");

        // Verify that the service method was called with the correct id parameter
        Object interestRateService;
        verify(service).obtainById(1);

        // Verify that the response is not null and has a status of OK
        Assert.assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that the response body is not null and has the correct values
        InterestRateRQRS responseBody = response.getBody();
        Assert.assertNotNull(responseBody);
        assertEquals(interestRate.getId(), responseBody.getId());
        assertEquals(interestRate.getInterestRate(), responseBody.getInterestRate());
    }

    @Test
    public void testCreate() throws CRUDException {
        // Create a mock InterestRateRQRS object
        InterestRateRQRS interestRateRQRS = InterestRateRQRS.builder()
                .id(1)
                .name("Rate 1")
                .interestRate(BigDecimal.valueOf(0.05))
                .state("ACT")
                .build();
        // Call the create() method with the mock InterestRateRQRS object
        doNothing().when(service).create(any(InterestRate.class));
        ResponseEntity<?> response = controller.create(interestRateRQRS);

        // Verify that the service method was called with the correct InterestRate object
        verify(service).create(any(InterestRate.class));

        // Verify that the response is not null and has a status of OK
        Assert.assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCreateWithException() throws CRUDException {
        // Create a mock InterestRateRQRS object
        InterestRateRQRS interestRateRQRS = InterestRateRQRS.builder()
                .id(1)
                .name("Rate 1")
                .interestRate(BigDecimal.valueOf(0.05))
                .state("ACT")
                .build();

        // Set up the mock service to throw a CRUDException when create() is called
        doThrow(CRUDException.class).when(service).create(any(InterestRate.class));

        // Call the create() method with the mock InterestRateRQRS object
        ResponseEntity<?> response = controller.create(interestRateRQRS);

        // Verify that the service method was called with the correct InterestRate object
        verify(service).create(any(InterestRate.class));

        // Verify that the response is not null and has a status of BAD_REQUEST
        Assert.assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void testUpdate() throws Exception {
        // Crea el objeto InterestRateRQRS de prueba
        InterestRateRQRS interestrate = InterestRateRQRS.builder()
                .id(123)
                .name("Rate 1")
                .interestRate(BigDecimal.valueOf(2.5))
                .state("ACT")
                .build();

        // Crea el objeto InterestRate de prueba
        InterestRate ir = new InterestRate();
        ir.setInterestRate(BigDecimal.valueOf(2.5));

        // Configura el comportamiento del servicio mock
        doNothing().when(service).update(eq(interestrate.getId()),any(InterestRate.class));
        when(service.obtainById(eq(123))).thenReturn(ir);

        // Llama al método controlador y verifica la respuesta
        ResponseEntity<InterestRateRQRS> response = controller.update("123", interestrate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interestrate.getInterestRate(), response.getBody().getInterestRate());
    }

    @Test
    public void testDelete() throws Exception {
        // Configura el comportamiento del servicio mock
        doNothing().when(service).inactivate(anyInt());

        // Llama al método controlador y verifica la respuesta
        ResponseEntity<?> response = controller.delete("123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private final String id = "1";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doThrow(new CRUDException(404, "Interest rate not found")).when(service).inactivate(Integer.parseInt(id));
    }

    @Test
    public void testDeleteWithException() {
        ResponseEntity<?> response = controller.delete(id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, times(1)).inactivate(Integer.parseInt(id));
    }
}