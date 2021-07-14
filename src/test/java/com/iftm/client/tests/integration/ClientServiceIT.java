package com.iftm.client.tests.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ClientServiceIT {

	@Autowired
	private ClientService service;

	private Long existingId;
	private Long nonExistingId;
	private Long countClientByIncome;
	private Long countTotalClient;
	private PageRequest pageRequest;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = Long.MAX_VALUE;
		pageRequest = PageRequest.of(0, 6);
		countClientByIncome = 5L;
		countTotalClient = 12L;
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

	}

	@Test
	public void findByIncomeShouldReturnClientWhenClientIncomeIsGreaterThanOrEqualsToValue() {

		Double income = 4000.00;

		Page<ClientDTO> result = service.findByIncome(income, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());

	}

	@Test
	public void findAllShouldReturnAllClients() {
		List<ClientDTO> result = service.findAll();

		Assertions.assertEquals(countTotalClient, result.size());

	}

}
