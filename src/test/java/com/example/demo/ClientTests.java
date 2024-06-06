package com.example.demo;

import com.example.demo.entities.Client;
import com.example.demo.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientTests {

	@Autowired
	private ClientRepository clientRepository;

	@Test
	public void testCreateClient(){
		Client client = new Client("name", "email", "phone");
		clientRepository.save(client);
	}

	@Test
	public void testCreateManyClients(){
		List<Client> list = new ArrayList<>();
		int countOfClients = 100000;
		for(int i = 0; i < countOfClients;++i){
			list.add(new Client(Integer.valueOf(i).toString(), Integer.valueOf(i).toString(), Integer.valueOf(i).toString()));
		}
		clientRepository.saveAll(list);
	}
	@Test
	public void testMillionStrings(){
		List<Client> list = new ArrayList<>();
		int countOfClients = 1000000;
		for(int i = 0; i < countOfClients;++i){
			list.add(new Client(Integer.valueOf(i).toString(), Integer.valueOf(i).toString(), Integer.valueOf(i).toString()));
		}
		Iterable<Client> id = clientRepository.saveAll(list);
		for(Client client : id){
			clientRepository.findById(client.getId());
		}
	}

}
