package com.example.demo;

import com.example.demo.controllers.ClientController;
import com.example.demo.entities.Client;
import com.example.demo.model.ClientModel;
import com.example.demo.repositories.ClientRepository;
import com.example.demo.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
public class ClientControllerTest {

	@Autowired
	private ClientService clientService;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	private Iterable<Client> id;

	@BeforeEach
	void setUp(){
		MockitoAnnotations.openMocks(this);
		//objectMapper = new ObjectMapper();
	}

	Semaphore sem = new Semaphore(10);
	List<Long> countMillisToOneResponse = new ArrayList<>();

	@Test
	void findClients() throws Exception {
		setClients();
		checkClients();
		sem.acquire(10);
		Collections.sort(countMillisToOneResponse);
		System.out.println("95:" + percentile(countMillisToOneResponse, 95));
		System.out.println("99:" + percentile(countMillisToOneResponse, 99));
		System.out.println("the median: " + countMillisToOneResponse.get(countMillisToOneResponse.size()/2));
	}

	private void setClients(){
		List<Client> list = new ArrayList<>();
		int countOfClients = 1000000;
		for(int i = 0; i < countOfClients;++i){
			list.add(new Client(Integer.valueOf(i).toString(), Integer.valueOf(i).toString(), Integer.valueOf(i).toString()));
		}
		id = clientService.createAll(list).join();
	}
	private void checkClients() throws InterruptedException {
		for(Client ids : id){
			sem.acquire();
			new Thread(() -> {
				try {
					Long from = System.currentTimeMillis();
					mockMvc.perform(MockMvcRequestBuilders.get("/clients/" + ids.getId()))
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.id").value(ids.getId()));
					Long to = System.currentTimeMillis();
					countMillisToOneResponse.add(to-from);
					sem.release();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).start();
		}
	}

	public long percentile(List<Long> latencies, double percentile) {
		int index = (int) Math.ceil(percentile / 100.0 * latencies.size());
		return latencies.get(index-1);
	}

}
