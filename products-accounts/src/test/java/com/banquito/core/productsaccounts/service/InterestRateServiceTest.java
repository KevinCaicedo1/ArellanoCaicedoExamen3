package com.banquito.core.productsaccounts.service;
import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.InterestRate;
import com.banquito.core.productsaccounts.repository.InterestRateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InterestRateServiceTest {

    @Mock
    private InterestRateRepository repository;

    @InjectMocks
    private InterestRateService service;



    @Test
    public void testListAllActives() {
        List<InterestRate> mockRates = new ArrayList<>();
        InterestRate rate = new InterestRate();
        rate.setId(1);
        rate.setInterestRate(new BigDecimal(0.5));
        rate.setState("ACT");
        InterestRate rate2 = new InterestRate();
        rate2.setId(2);
        rate2.setInterestRate(new BigDecimal(0.75));
        rate2.setState("ACT");

        mockRates.add(rate);
        mockRates.add(rate2);

        when(repository.findByState("ACT")).thenReturn(mockRates);

        List<InterestRate> result = service.listAllActives();

        assertEquals(mockRates, result);
        verify(repository, times(1)).findByState("ACT");
    }

    @Test
    public void testObtainById() {
        Integer id = 1;
        InterestRate mockRate = new InterestRate();
        mockRate.setId(id);
        mockRate.setInterestRate(new BigDecimal(0.5));
        Optional<InterestRate> mockRateOpt = Optional.of(mockRate);
        when(repository.findById(id)).thenReturn(mockRateOpt);

        InterestRate result = service.obtainById(id);

        assertEquals(mockRate, result);
        verify(repository, times(1)).findById(id);
    }

    @Test(expected = CRUDException.class)
    public void testObtainByIdNotFound() {
        Integer id = 1;
        Optional<InterestRate> mockRateOpt = Optional.empty();
        when(repository.findById(id)).thenReturn(mockRateOpt);

        service.obtainById(id);
    }

    @Test
    public void testCreate() throws CRUDException {
        InterestRate mockRate = new InterestRate();
        mockRate.setId(1);
        mockRate.setState("ACT");
        mockRate.setInterestRate(new BigDecimal(0.5));

        when(repository.save(mockRate)).thenReturn(mockRate);

        service.create(mockRate);

        verify(repository, times(1)).save(mockRate);
    }

    @Test(expected = CRUDException.class)
    public void testCreateException() throws CRUDException {
        InterestRate mockRate = new InterestRate();
        mockRate.setId(1);
        mockRate.setState("ACT");
        mockRate.setInterestRate(new BigDecimal(0.5));
        doThrow(new RuntimeException("Test exception")).when(repository).save(mockRate);

        service.create(mockRate);
    }
    @Test
    public void testUpdateInterestRate() throws CRUDException {
        // Arrange
        Integer id = 1;
        InterestRate interestRateToUpdate = new InterestRate();
//        "Updated Interest Rate", 0.05
        interestRateToUpdate.setName("Updated Interest Rate");
        interestRateToUpdate.setInterestRate(BigDecimal.valueOf(0.05));
        InterestRate existingInterestRate = new InterestRate();
//        "Interest Rate", 0.01
        existingInterestRate.setName("Interest Rate");
        existingInterestRate.setInterestRate(BigDecimal.valueOf(0.01));
        when(repository.findById(id)).thenReturn(Optional.of(existingInterestRate));
        when(repository.save(existingInterestRate)).thenReturn(existingInterestRate);

        // Act
        service.update(id, interestRateToUpdate);

        // Assert
        assertEquals(interestRateToUpdate.getName(), existingInterestRate.getName());
        assertEquals(interestRateToUpdate.getInterestRate(), existingInterestRate.getInterestRate());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(existingInterestRate);
    }

    @Test(expected = CRUDException.class)
    public void testUpdateInterestRateNonExisting() throws CRUDException {
        // Arrange
        Integer id = 1;
        InterestRate interestRateToUpdate = new InterestRate();
//        "Updated Interest Rate", 0.05
        interestRateToUpdate.setName("Updated Interest Rate");
        interestRateToUpdate.setInterestRate(BigDecimal.valueOf(0.05));
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        service.update(id, interestRateToUpdate);

        // Assert (will throw exception)
    }
    @Test
    public void testInactivate() throws Exception {
        // Arrange
        Integer id = 1;
        InterestRate interestRate = new InterestRate();
        interestRate.setId(id);
        interestRate.setState("ACT");
        interestRate.setStart(new Date());
        when(repository.findById(id)).thenReturn(Optional.of(interestRate));

        // Act
        service.inactivate(id);

        // Assert
        verify(repository).save(interestRate);
        assertEquals("INA", interestRate.getState());
        assertNotNull(interestRate.getEnd());
    }
}
