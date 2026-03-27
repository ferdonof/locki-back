package com.ferdonof.locki.controllers;

import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.racks.enums.RackStatus;
import com.ferdonof.locki.repositories.LockerRepository;
import com.ferdonof.locki.repositories.RackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class LockersControllerTestIT {

	private static final String LOCKERS_URL = "/admin/lockers";

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LockerRepository lockerRepository;

	@Autowired
	private RackRepository rackRepository;

	@BeforeEach
	void setUp() {
		this.lockerRepository.deleteAll();
		this.rackRepository.deleteAll();
	}

	@Test
	void create_whenValidRequest_shouldReturn201() throws Exception {
		final RackEntity savedRack = this.rackRepository.saveAndFlush(RackEntity.builder()
				.number(1)
				.status(RackStatus.ACTIVE)
				.build());

		final String requestBody = """
				{
				  "number": 10,
				  "rackId": "%s",
				  "status": "AVAILABLE",
				  "latchStatus": "CLOSED"
				}
				""".formatted(savedRack.getId());

		this.mockMvc
				.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.number").value(10))
				.andExpect(jsonPath("$.status").value("AVAILABLE"))
				.andExpect(jsonPath("$.latchStatus").value("CLOSED"));

		assertThat(this.lockerRepository.count()).isEqualTo(1);

		final LockerEntity persisted = this.lockerRepository.findAll().getFirst();
		assertThat(persisted.getNumber()).isEqualTo(10);
		assertThat(persisted.getRack()).isNotNull();
		assertThat(persisted.getRack().getId()).isEqualTo(savedRack.getId());
		assertThat(persisted.getCreatedAt()).isNotNull();
	}

	@Test
	void create_whenRackDoesNotExist_shouldReturn201WithNullRack() throws Exception {
		final ClassPathResource resource = new ClassPathResource("mocks/requests/lockers/create-locker.json");
		this.mockMvc
				.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(resource.getContentAsByteArray()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.number").value(10))
				.andExpect(jsonPath("$.rack").isEmpty());

		assertThat(this.lockerRepository.count()).isEqualTo(1);

		final LockerEntity persisted = this.lockerRepository.findAll().getFirst();
		assertThat(persisted.getRack()).isNull();
	}

	@Test
	void create_whenMissingRequiredAttributes_shouldReturn400() throws Exception {
		this.mockMvc
				.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());

		assertThat(this.lockerRepository.count()).isZero();
	}

	@Test
	void create_whenDataIntegrityViolation_shouldReturn400() throws Exception {
		final RackEntity savedRack = this.rackRepository.saveAndFlush(RackEntity.builder()
				.number(1)
				.status(RackStatus.ACTIVE)
				.build());

		final String requestBody = """
				{
				  "number": 10,
				  "rackId": "%s",
				  "status": "AVAILABLE"
				}
				""".formatted(savedRack.getId());

		this.mockMvc
				.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.title").value("Error"));

		assertThat(this.lockerRepository.count()).isZero();
	}
}
