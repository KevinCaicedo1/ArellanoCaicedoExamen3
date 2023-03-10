package com.banquito.core.branches.controller;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.banquito.core.branches.controller.dto.BranchRQRS;
import com.banquito.core.branches.controller.mapper.BranchMapper;
import com.banquito.core.branches.exception.CRUDException;
import com.banquito.core.branches.model.Branch;
import com.banquito.core.branches.service.BranchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public class BranchControllerTest {

    @Mock
    private BranchService branchService;

    @InjectMocks
    private BranchController branchController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtainAll() {
        List<Branch> mockBranches = new ArrayList<>();
        Branch branch = new Branch();
        branch.setCode("001");
        branch.setName("Branch 1");
        mockBranches.add(branch);
        Branch branch2 = new Branch();
        branch2.setCode("002");
        branch2.setName("Branch 2");
        mockBranches.add(branch2);
        when(branchService.getAll()).thenReturn(mockBranches);

        ResponseEntity<List<BranchRQRS>> response = branchController.obtainAll();

        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Branch 1", response.getBody().get(0).getName());
        Assertions.assertEquals("Branch 2", response.getBody().get(1).getName());
    }

    @Test
    public void testObtainByCode() {
        String code = "BR001";
        Branch mockBranch = new Branch();

        mockBranch.setCode(code);
        mockBranch.setName("Branch 1");

        when(branchService.lookByCode(code)).thenReturn(mockBranch);

        ResponseEntity<BranchRQRS> response = branchController.obtainByCode(code);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Branch 1", response.getBody().getName());
        Assertions.assertEquals(code, response.getBody().getCode());
    }

    @Test
    public void testObtainByCodeNotFound() {
        String code = "BR002";
        when(branchService.lookByCode(code)).thenReturn(null);

        ResponseEntity<BranchRQRS> response = branchController.obtainByCode(code);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Mock
    private ResponseEntity<BranchRQRS> responseEntity;

    @Test
    public void testUpdateBranch() throws CRUDException {
        String code = "BR001";
        Branch branch = new Branch();
        branch.setId("12");
        branch.setCode(code);
        branch.setName("Branch 1");

        BranchRQRS branchRQRS = BranchRQRS.builder().id("12").code(code).name("Branch 1").build();
        //set test data in branchRQRS and branch objects

        when(branchService.lookByCode(code)).thenReturn(branch);
//        when update returns void
        doNothing().when(branchService).update(code,branch);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseEntity.getBody()).thenReturn(branchRQRS);


        ResponseEntity<BranchRQRS> result = branchController.update(code, branchRQRS);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


        @Test
        public void testCreateBranch() throws CRUDException {
            String code = "BR001";
            Branch branch = new Branch();
            branch.setId("12");
            branch.setCode(code);
            branch.setName("Branch 1");

            BranchRQRS branchRQRS = BranchRQRS.builder().id("12").code(code).name("Branch 1").build();
            //set test data in branchRQRS and branch objects

            when(branchService.lookByCode(code)).thenReturn(null);
            doNothing().when(branchService).create(branch);
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            when(responseEntity.getBody()).thenReturn(branchRQRS);

            ResponseEntity<BranchRQRS> result = branchController.create(branchRQRS);
            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());
        }

        @Test
        public void testCreateBranchAlreadyExists() throws CRUDException {
            String code = "BR001";
            Branch branch = new Branch();
            branch.setId("12");
            branch.setCode(code);
            branch.setName("Branch 1");

            BranchRQRS branchRQRS = BranchRQRS.builder().id("12").code(code).name("Branch 1").build();
            //set test data in branchRQRS and branch objects

            when(branchService.lookByCode(code)).thenReturn(branch);
            doThrow(CRUDException.class).when(branchService).create(branch);
            when(responseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
            when(responseEntity.getBody()).thenReturn(branchRQRS);

            ResponseEntity<BranchRQRS> result = branchController.create(branchRQRS);
            assertNotNull(result);
            assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        }
}