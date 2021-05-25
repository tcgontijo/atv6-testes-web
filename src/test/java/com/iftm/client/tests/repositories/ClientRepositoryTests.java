package com.iftm.client.tests.repositories;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {

	@Autowired
	private ClientRepository repository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalClients;
	private Long countClientByIncome;
	private String existingName;
	private Long countClientBirthAt1956;
	private Long countClientBirthAfter2000;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 2L;
		nonExistingId = Long.MAX_VALUE;
		countTotalClients = 12L;
		countClientByIncome = 5L;
		existingName = "Carolina Maria de Jesus";
		countClientBirthAt1956 = 2L;
		countClientBirthAfter2000 = 1L;
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId);
		Optional<Client> result = repository.findById(existingId);
		Assertions.assertFalse(result.isPresent());

	}

	@Test
	public void deleteShouldThrowExceptionWhenIdDoesnotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});

	}

	@Test
	public void findByIcomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {

		PageRequest pageRequest = PageRequest.of(0, 10);
		Double income = 4000.00;

		Page<Client> result = repository.findByIncome(income, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());

	}

	@Test
	public void findByNameShouldReturnClientsWhenNameExists() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<Client> result = repository.findByNameContainingIgnoreCase(existingName, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(existingName, result.getContent().get(0).getName());

	}

	@Test
	public void findByNameShouldReturnClientsWhenNameExistsIgnoringCase() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		String nameIgnoreCase = existingName.toUpperCase();

		Page<Client> result = repository.findByNameContainingIgnoreCase(nameIgnoreCase, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(existingName, result.getContent().get(0).getName());

	}

	@Test
	public void findByNameShouldReturnAllClientsWhenEmptyName() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<Client> result = repository.findByNameContainingIgnoreCase("", pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalClients, result.getTotalElements());

	}

	@Test
	public void findByYearOfBirthDateShouldReturnClientsWhenClientsWereBornAtYear() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<Client> result = repository.findByYearOfBirthDate(1956, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientBirthAt1956, result.getTotalElements());

	}

	@Test
	public void findByBirthDateAfterShouldReturnClientsWhenClientsWereBornAfter() throws ParseException {

		PageRequest pageRequest = PageRequest.of(0, 10);

		String bD1 = "01/01/2000";
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(bD1);
		Instant bD2 = date.toInstant();

		Page<Client> result = repository.findByBirthDateAfter(bD2, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientBirthAfter2000, result.getTotalElements());

	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Client client = ClientFactory.createClient();
		client.setId(null);

		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());

		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients + 1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client);

	}

	@Test
	public void saveShouldUpdateClientNameWhenIdExists() {

		Client client = ClientFactory.createClient();

		String updatedName = client.getName() + "da Silva";

		client.setName(updatedName);


		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());

		Assertions.assertEquals(updatedName, client.getName());
		Assertions.assertSame(result.get(), client);

	}

	@Test
	public void saveShouldUpdateClientChildrenWhenIdExists() {

		Client client = ClientFactory.createClient();

		Integer updatedChildren = client.getChildren() + 1;

		client.setChildren(updatedChildren);

		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());

		Assertions.assertEquals(updatedChildren, client.getChildren());
		Assertions.assertSame(result.get(), client);

	}

	@Test
	public void saveShouldUpdateClientBirthDateWhenIdExists() {

		Client client = ClientFactory.createClient();

		Instant updatedBirthDate = client.getBirthDate().plus(30, ChronoUnit.DAYS);

		client.setBirthDate(updatedBirthDate);

		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());

		Assertions.assertEquals(updatedBirthDate, client.getBirthDate());
		Assertions.assertSame(result.get(), client);

	}

}
