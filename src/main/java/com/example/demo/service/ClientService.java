package com.example.demo.service;

import com.example.demo.entities.Client;
import com.example.demo.model.ClientModel;
import com.example.demo.repositories.ClientRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Async("asyncExecutor")
public class ClientService {
	private final ClientRepository clientRepository;

	public ClientService(ClientRepository taskRepository) {
		this.clientRepository = taskRepository;
	}

	public CompletableFuture<Client> create(Client client) {
		return CompletableFuture.completedFuture(clientRepository.save(client));
	}

	public CompletableFuture<Iterable<Client>> createAll(List<Client> client) {
		return CompletableFuture.completedFuture(clientRepository.saveAll(client));
	}

	public CompletableFuture<List<Client>> findAll() {
		List<Client> clients = new ArrayList<>();
		clientRepository.findAll().forEach(clients::add);

		return CompletableFuture.completedFuture(clients);
	}

	public CompletableFuture<Optional<Client>> findById(int id) {
		return CompletableFuture.completedFuture(clientRepository.findById(id));
	}

	public CompletableFuture<Iterable<Client>> findByAllId(Iterable<Integer> id) {
		return CompletableFuture.completedFuture(clientRepository.findAllById(id));
	}

	public CompletableFuture<Client> update(Client taskToUpdate) {
		return CompletableFuture.completedFuture(clientRepository.save(taskToUpdate));
	}

	public void delete(int id) {
		new Thread(()->clientRepository.deleteById(id)).start();
	}
	public static CompletableFuture<Client> toClient(ClientModel clientModel){
		Client client = new Client();
		client.setName(clientModel.getName());
		client.setEmail(clientModel.getEmail());
		client.setPhone(clientModel.getPhone());
		return CompletableFuture.completedFuture(client);
	}
}
