package com.banquito.core.productsaccounts.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.ProductAccount;
import com.banquito.core.productsaccounts.repository.ProductAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductAccountServiceTest {

    @Mock
    private ProductAccountRepository repository;

    @InjectMocks
    private ProductAccountService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateProductAccount() throws CRUDException {
        // create the input object
        ProductAccount productAccount = new ProductAccount();
        productAccount.setName("Test Product Account");

        // define the behavior of the mock object
        when(repository.save(productAccount)).thenReturn(productAccount);

        // call the create method
        service.create(productAccount);

        // verify that the creation date was set
        Assertions.assertNotNull(productAccount.getCreationDate());
    }

    @Test
    public void testCreateProductAccountException() throws CRUDException {
        // create the input object
        ProductAccount productAccount = new ProductAccount();
        productAccount.setName("Test Product Account");

        // define the behavior of the mock object
        when(repository.save(productAccount)).thenThrow(new RuntimeException("Error in Product Account creation"));

        // call the create method
        Assertions.assertThrows(CRUDException.class, () -> {
            service.create(productAccount);
        });
    }

    @Test
    public void testObtainProductAccountById() throws CRUDException {
        // define the behavior of the mock object
        String id = "123";
        ProductAccount productAccount = new ProductAccount();
        productAccount.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(productAccount));

        // call the obtainById method
        ProductAccount result = service.obtainById(id);

        // verify that the correct object was returned
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testObtainProductAccountByIdNotFound() {
        // define the behavior of the mock object
        String id = "123";
        when(repository.findById(id)).thenReturn(Optional.empty());

        // call the obtainById method
        Assertions.assertThrows(CRUDException.class, () -> service.obtainById(id));
    }

    @Test
    public void testListAllActives() {
        // Crear una lista de cuentas de producto activas para simular la respuesta del repositorio
        List<ProductAccount> activeAccounts = new ArrayList<>();
        activeAccounts.add(new ProductAccount());
        activeAccounts.add(new ProductAccount());
        activeAccounts.add(new ProductAccount());

        // Configurar el comportamiento simulado del repositorio
        when(repository.findByState("ACT")).thenReturn(activeAccounts);

        // Llamar a la función a probar y verificar la respuesta
        List<ProductAccount> result = service.listAllActives();
        assertEquals(3, result.size());
    }
}