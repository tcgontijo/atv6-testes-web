package com.iftm.client.tests.factory;

import java.time.Instant;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;

public class ClientFactory {

	public static Client createClient() {
		
		return new Client(1L, "Pablo Alberto", "12345678900", 2000.00, Instant.parse("1958-09-20T08:00:00Z"), 1);
		
	}
	
	public static Client createEmptyClient() {
		return new Client();
	}

	public static ClientDTO createClientDTO(Long id) {
		ClientDTO dto = createClientDTO();
		dto.setId(id);
		return dto;
	}
	
	public static ClientDTO createClientDTO() {		
		return new ClientDTO(createClient());
	}
	
	public static ClientDTO createEmptyClientDTO() {
		return new ClientDTO(createEmptyClient());
	}

}
