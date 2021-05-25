package com.iftm.client.repositories;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iftm.client.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	
	@Query(value = "SELECT DISTINCT obj FROM Client obj WHERE "
			+ "obj.income >= :income")
	Page<Client> findByIncome(Double income, Pageable pageable);
	
	
	//Busca Clientes por nome-
	
	Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);
	
	
	//Busca Clientes por ano de nascimento
	
	@Query(value = "SELECT * FROM tb_client WHERE year(BIRTH_DATE)= ?1", nativeQuery=true)
	Page<Client> findByYearOfBirthDate(Integer birthDateYear, Pageable pageable);
	
	//Busca Clientes que nasceram após determinado ano
	
	Page<Client> findByBirthDateAfter(Instant birthDate, Pageable pageable);
	

}
