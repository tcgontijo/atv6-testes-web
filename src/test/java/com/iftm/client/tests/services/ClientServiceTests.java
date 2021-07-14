package com.iftm.client.tests.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
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

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	
	private PageRequest pageRequest;
	private PageImpl<Client> page;
	private Client client;
	private Client nonExistingClient;
	private ClientDTO clientDTO;
	private Client emptyClient;
	private ClientDTO emptyClientDTO;
	private Double income;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = Long.MAX_VALUE;
		dependentId = 4L;
		client = ClientFactory.createClient();
		clientDTO = ClientFactory.createClientDTO();
		nonExistingClient = ClientFactory.createClient();
		nonExistingClient.setId(nonExistingId);
		emptyClient = ClientFactory.createEmptyClient();
		emptyClientDTO = ClientFactory.createEmptyClientDTO();
		pageRequest = PageRequest.of(0, 6);
		page = new PageImpl<>(List.of(client));
		income = 4000.00;
		
		//Configurando comportamento para o Mock para método Delete
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		//Configurando comportamento para o Mock para método FindAll
		Mockito.when(repository.findAll(pageRequest))
			.thenReturn(page);
		
		//Configurando comportamento para o Mock para método FindByIncome
		Mockito.when(repository.findByIncome(ArgumentMatchers.anyDouble(), ArgumentMatchers.any()))
			.thenReturn(page);
	
		//Configurando comportamento para o Mock para método FindById
		Mockito.when(repository.findById(existingId))
		.thenReturn(Optional.of(client));
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
		
		//Configurando comportamento para o Mock para método Update
		Mockito.when(repository.save(client))
		.thenReturn(client);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).save(nonExistingClient);
		
		Mockito.when(repository.getOne(existingId))
		.thenReturn(client);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(nonExistingId);
		
		//Configurando comportamento para o Mock para método Insert		
		Mockito.when(repository.save(emptyClient))
		.thenReturn(emptyClient);
		
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependenceIntegrity() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Page<ClientDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertNotNull(result);
		Assertions.assertFalse(result.isEmpty());
		
		Mockito.verify(repository, Mockito.times(1)).findAll(pageRequest);
		
	}
	
	@Test
	public void findByIncomeShouldReturnPage() {
		
		Page<ClientDTO> result = service.findByIncome(income, pageRequest);
		
		Assertions.assertNotNull(result);
		Assertions.assertFalse(result.isEmpty());
		
		Mockito.verify(repository, Mockito.times(1)).findByIncome(income, pageRequest);
		
	}
	
	@Test
	public void findByIdShouldReturnClientDTOWhenIdExists() {
		
		ClientDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
		
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
		
	}
	
	@Test
	public void updateShouldReturnClientDTOWhenIdExists() {
		
		ClientDTO result = service.update(existingId, clientDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingId);
		
		Mockito.verify(repository, Mockito.times(1)).save(client);
		
	}
	
	@Test
	public void updateShouldThowResourceNotFoundExceptionWhenIdDoesntExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			clientDTO.setId(nonExistingId);
			service.update(nonExistingId, clientDTO);
		});
		
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
		
	}
	
	@Test
	public void insertShouldReturnClientDTO() {
		
		ClientDTO result = service.insert(emptyClientDTO);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).save(emptyClient);
		
	}

}
