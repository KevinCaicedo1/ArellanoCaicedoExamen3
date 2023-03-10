package com.banquito.core.productsaccounts.controller;

import com.banquito.core.productsaccounts.controller.dto.ProductAccountRQRS;
import com.banquito.core.productsaccounts.controller.mapper.ProductAccountMapper;
import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.ProductAccount;
import com.banquito.core.productsaccounts.service.ProductAccountService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ProductAccountControllerTest {

    @Mock
    private ProductAccountService productAccountService;

    @InjectMocks
    private ProductAccountController productAccountController;

    @Test
    public void testCreate() throws CRUDException {
        // Create a mock ProductAccountRQRS object
        ProductAccountRQRS productAccountRQRS = ProductAccountRQRS.builder()
                .id("11l")
                .name("Product Account 1")
                .build();

        // Call the create() method with the mock ProductAccountRQRS object
        doNothing().when(productAccountService).create(any(ProductAccount.class));
        ResponseEntity<?> response = productAccountController.create(productAccountRQRS);

        // Verify that the service method was called with the correct ProductAccount object
        verify(productAccountService).create(any(ProductAccount.class));

        // Verify that the response is not null and has a status of OK
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCreateWithException() throws CRUDException {
        // Create a mock ProductAccountRQRS object
        ProductAccountRQRS productAccountRQRS = ProductAccountRQRS.builder()
                .id("11l")
                .name("Product Account 1")
                .build();

        // Set up the mock service to throw a CRUDException when create() is called
        doThrow(CRUDException.class).when(productAccountService).create(any(ProductAccount.class));

        // Call the create() method with the mock ProductAccountRQRS object
        ResponseEntity<?> response = productAccountController.create(productAccountRQRS);

        // Verify that the service method was called with the correct ProductAccount object
        verify(productAccountService).create(any(ProductAccount.class));

        // Verify that the response is not null and has a status of BAD_REQUEST
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testObtainByCode() {
        // Create a mock ProductAccount object
        ProductAccount productAccount = new ProductAccount();
        productAccount.setId("11l");
        productAccount.setName("Test Account");

        // Set up the mock service to return the mock ProductAccount object when obtainById() is called
        when(productAccountService.obtainById(anyString())).thenReturn(productAccount);

        // Call the obtainByCode() method with a mock id
        ResponseEntity<ProductAccountRQRS> response = productAccountController.obtainByCode("11l");

        // Verify that the service method was called with the correct id
        verify(productAccountService).obtainById("11l");

        // Verify that the response is not null and has a status of OK
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that the response body is not null and has the correct values
        ProductAccountRQRS responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("11l", responseBody.getId());
        assertEquals("Test Account", responseBody.getName());
    }

    @Test
    public void testObtainByCodeNotFound() {
        // Set up the mock service to return null when obtainById() is called
        when(productAccountService.obtainById(anyString())).thenReturn(null);

        // Call the obtainByCode() method with a mock id
        ResponseEntity<ProductAccountRQRS> response = productAccountController.obtainByCode("1");

        // Verify that the service method was called with the correct id
       verify(productAccountService).obtainById("1");

        // Verify that the response is not null and has a status of NOT_FOUND
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void obtainAll_shouldReturnAllActiveProductAccounts() {
        // given
        ProductAccount productAccount1 = new ProductAccount();
        ProductAccount productAccount2 = new ProductAccount();
        List<ProductAccount> productAccounts = Arrays.asList(productAccount1, productAccount2);
        when(productAccountService.listAllActives()).thenReturn(productAccounts);

        // when
        ResponseEntity<List<ProductAccountRQRS>> response = productAccountController.obtainAll();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(ProductAccountMapper.mapToList(productAccounts), response.getBody());
    }

}