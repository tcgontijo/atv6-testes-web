package com.iftm.client.tests.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.DatabaseException;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {

	@InjectMocks
	private ClientService service;

	@Mock
	private ClientRepository repository;

	private Long existingID;
	private Long nonExistingID;
	private Long dependentID;
	private Client client;

	@BeforeEach
	void setUp() throws Exception {
		existingID = 1L;
		nonExistingID = Long.MAX_VALUE;
		dependentID = 4L;
		client = ClientFactory.createClient();
		
		//Configurando comportamento para o Mock
		Mockito.doNothing().when(repository).deleteById(existingID);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingID);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentID);
		
		Mockito.when(repository.findById(existingID)).thenReturn(Optional.of(client));
		Mockito.when(repository.findById(nonExistingID)).thenReturn(Optional.empty());

	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingID);
		});
		
		//Mockito.verify(repository).deleteById(existingID);
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingID);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoentExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingID);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingID);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependenceIntegrity() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentID);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentID);
	}

}
