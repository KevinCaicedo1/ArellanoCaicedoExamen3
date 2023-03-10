package com.banquito.core.branches.service;
import com.banquito.core.branches.exception.CRUDException;
import com.banquito.core.branches.model.Branch;
import com.banquito.core.branches.repository.BranchRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchService branchService;

    @Before
    public void setUp() {
        Branch branch = new Branch();
        branch.setCode("1");
        branch.setName("Branch 123");
        when(branchRepository.findById(anyString())).thenReturn(Optional.of(branch));
        when(branchRepository.findById(anyString()))
                .thenReturn(Optional.of(branch));
    }

    @Test
    public void testLookById() throws CRUDException, CRUDException {
        Branch result = branchService.lookById("1");
        assertEquals("Branch 123", result.getName());
    }

    @Test(expected = CRUDException.class)
    public void testLookByIdNotFound() throws CRUDException {
        when(branchRepository.findById(anyString())).thenReturn(Optional.empty());
        branchService.lookById("2");

    }

    ;

    @Test
    public void testLookByCode() {
        String code = "001";
        Branch branch = new Branch();

        branch.setCode("001");
        branch.setName("Branch 001");

        when(branchRepository.findByCode(code)).thenReturn(branch);
        Branch branchTest = branchService.lookByCode(code);
        assertEquals(branchTest, branch);
    }

    @Test
    public void testCreateBranch() throws CRUDException {
        // Arrange
        Branch branch = new Branch();
        branch.setCode("001");
        branch.setName("Branch 001");
        branch.setId("1L");
        when(branchRepository.save(branch)).thenReturn(branch);

        // Act
        branchService.create(branch);

        // Assert
        verify(branchRepository, times(1)).save(branch);
    }

    @Test
    public void testGetAllBranches() {
        // Arrange
        Branch branch = new Branch();
        branch.setCode("001");
        branch.setName("Branch 001");
        branch.setId("1");
        Branch branch2 = new Branch();
        branch2.setCode("002");
        branch2.setName("Branch 002");
        branch2.setId("2");
        List<Branch> branches = Arrays.asList(
                branch,
                branch2
        );
        when(branchRepository.findAll()).thenReturn(branches);

        // Act
        List<Branch> result = branchService.getAll();

        // Assert
        assertEquals(branches, result);
        verify(branchRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateBranch() throws CRUDException {
        // Arrange
        String code = "B001";
        Branch branchToUpdate = new Branch();
        Branch existingBranch = new Branch();
        existingBranch.setCode("B001");
        existingBranch.setName("Branch 001");
        existingBranch.setId("1");
        branchToUpdate.setCode("B001");
        branchToUpdate.setName("Branch 001 Updated");
        branchToUpdate.setId("1");
        when(branchRepository.findByCode(code)).thenReturn(existingBranch);
        when(branchRepository.save(existingBranch)).thenReturn(existingBranch);
        // Act
        branchService.update(code, branchToUpdate);
        // Assert
        assertEquals(branchToUpdate.getName(), existingBranch.getName());
        verify(branchRepository, times(1)).findByCode(code);
        verify(branchRepository, times(1)).save(existingBranch);
    }

    @Test(expected = CRUDException.class)
    public void testUpdateBranchNotFound() throws CRUDException {
        // Arrange
        String code = "B001";
        Branch branchToUpdate = new Branch();
        branchToUpdate.setCode("B001");
        branchToUpdate.setName("Branch 001 Updated");
        branchToUpdate.setId("1");
        when(branchRepository.findByCode(code)).thenReturn(null);
        // Act
        branchService.update(code, branchToUpdate);
        // Assert
        verify(branchRepository, times(1)).findByCode(code);
    }
}
