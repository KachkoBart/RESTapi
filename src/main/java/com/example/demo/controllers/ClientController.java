package com.example.demo.controllers;

import com.example.demo.entities.Client;
import com.example.demo.model.ClientModel;
import com.example.demo.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
public class ClientController {
	public ClientService clientService;

	public ClientController(ClientService clientService){
		this.clientService = clientService;
	}

	@PostMapping("/clients")
	public ResponseEntity<Client> createClient(@RequestBody ClientModel clientModel) throws ExecutionException, InterruptedException {
		Client clientCreated = clientService.create(ClientService.toClient(clientModel).join()).join();

		return new ResponseEntity<>(clientCreated, HttpStatus.CREATED);
	}

	@GetMapping("/clients")
	public ResponseEntity<List<Client>> allClients() {
		List<Client> clients = clientService.findAll().join();

		return new ResponseEntity<>(clients, HttpStatus.OK);
	}
	@GetMapping("/clients/{id}")
	public ResponseEntity<Client> oneClient(@PathVariable int id) {
		Optional<Client> optionalClient = clientService.findById(id).join();

		return optionalClient.map(client -> new ResponseEntity<>(client, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@DeleteMapping("/clients/{id}")
	public ResponseEntity<Void> deleteClient(@PathVariable int id) {
		clientService.delete(id);

		return ResponseEntity.noContent().build();
	}
	@PatchMapping("/clients/{id}")
	public ResponseEntity<Client> updateClient(@PathVariable int id, @RequestBody ClientModel clientModel) {
		Optional<Client> optionalClient = clientService.findById(id).join();

		if (optionalClient.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Client client = optionalClient.get();
		client.setName(clientModel.getName());
		client.setEmail(clientModel.getEmail());
		client.setPhone(clientModel.getPhone());

		Client taskUpdated = clientService.update(client).join();

		return new ResponseEntity<>(taskUpdated, HttpStatus.OK);
	}
}
