package com.ferdonof.locki.controllers;

import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

	private RackEntity createRack() {
		return this.rackRepository.saveAndFlush(RackEntity.builder().number(1).status(RackStatus.ACTIVE).build());
	}

	private LockerEntity createLocker(RackEntity rack) {
		return this.lockerRepository.saveAndFlush(LockerEntity.builder().number(10).rack(rack)
				.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.CLOSED).build());
	}

	@Test
	void create_whenValidRequest_shouldReturn201() throws Exception {
		final RackEntity savedRack = this.createRack();

		final String requestBody = """
				{
				  "number": 10,
				  "rackId": "%s",
				  "status": "AVAILABLE",
				  "latchStatus": "CLOSED"
				}
				""".formatted(savedRack.getId());

		this.mockMvc.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.number").value(10)).andExpect(jsonPath("$.status").value("AVAILABLE"))
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
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.number").value(10)).andExpect(jsonPath("$.rack").isEmpty());

		assertThat(this.lockerRepository.count()).isEqualTo(1);

		final LockerEntity persisted = this.lockerRepository.findAll().getFirst();
		assertThat(persisted.getRack()).isNull();
	}

	@Test
	void create_whenMissingRequiredAttributes_shouldReturn400() throws Exception {
		this.mockMvc.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());

		assertThat(this.lockerRepository.count()).isZero();
	}

	@Test
	void create_whenDataIntegrityViolation_shouldReturn400() throws Exception {
		final RackEntity savedRack = this.createRack();

		final String requestBody = """
				{
				  "number": 10,
				  "rackId": "%s",
				  "status": "AVAILABLE"
				}
				""".formatted(savedRack.getId());

		this.mockMvc.perform(post(LOCKERS_URL).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.title").value("Error"));

		assertThat(this.lockerRepository.count()).isZero();
	}

	@Test
	void getById_whenLockerExists_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		this.mockMvc.perform(get(LOCKERS_URL + "/{id}", locker.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(locker.getId().toString())).andExpect(jsonPath("$.number").value(10))
				.andExpect(jsonPath("$.status").value("AVAILABLE"))
				.andExpect(jsonPath("$.latchStatus").value("CLOSED"));
	}

	@Test
	void getById_whenLockerNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		this.mockMvc.perform(get(LOCKERS_URL + "/{id}", randomId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404)).andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Locker with id '%s' not found".formatted(randomId)));
	}

	@Test
	void update_whenValidRequest_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		final String requestBody = """
				{
				  "id": "%s",
				  "status": "MAINTENANCE",
				  "latchStatus": "LOCKED"
				}
				""".formatted(locker.getId());

		this.mockMvc
				.perform(put(LOCKERS_URL + "/{id}", locker.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(locker.getId().toString()))
				.andExpect(jsonPath("$.status").value("MAINTENANCE"))
				.andExpect(jsonPath("$.latchStatus").value("LOCKED"));

		final LockerEntity updated = this.lockerRepository.findById(locker.getId()).orElseThrow();
		assertThat(updated.getStatus()).isEqualTo(LockerStatus.MAINTENANCE);
		assertThat(updated.getLatchStatus()).isEqualTo(LatchStatus.LOCKED);
	}

	@Test
	void update_whenLockerNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		final String requestBody = """
				{
				  "id": "%s",
				  "latchStatus": "LOCKED"
				}
				""".formatted(randomId);

		this.mockMvc
				.perform(put(LOCKERS_URL + "/{id}", randomId).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Locker with id '%s' not found".formatted(randomId)));
	}

	@Test
	void update_whenMissingRequiredFields_shouldReturn400() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		this.mockMvc.perform(
				put(LOCKERS_URL + "/{id}", locker.getId()).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void update_whenChangingRack_shouldUpdateRack() throws Exception {
		final RackEntity rack1 = this.createRack();
		final LockerEntity locker = this.createLocker(rack1);

		final RackEntity rack2 = this.rackRepository
				.saveAndFlush(RackEntity.builder().number(2).status(RackStatus.ACTIVE).build());

		final String requestBody = """
				{
				  "id": "%s",
				  "rackId": "%s",
				  "latchStatus": "CLOSED"
				}
				""".formatted(locker.getId(), rack2.getId());

		this.mockMvc
				.perform(put(LOCKERS_URL + "/{id}", locker.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(locker.getId().toString()));

		final LockerEntity updated = this.lockerRepository.findById(locker.getId()).orElseThrow();
		assertThat(updated.getRack().getId()).isEqualTo(rack2.getId());
	}

	@Test
	void search_whenLockersExist_shouldReturnList() throws Exception {
		final RackEntity rack = this.createRack();
		this.createLocker(rack);
		this.lockerRepository.saveAndFlush(LockerEntity.builder().number(20).rack(rack).status(LockerStatus.RESERVED)
				.latchStatus(LatchStatus.LOCKED).build());

		this.mockMvc.perform(post(LOCKERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 10, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void search_whenFilterByStatus_shouldReturnFilteredResults() throws Exception {
		final RackEntity rack = this.createRack();
		this.createLocker(rack); // AVAILABLE
		this.lockerRepository.saveAndFlush(LockerEntity.builder().number(20).rack(rack).status(LockerStatus.MAINTENANCE)
				.latchStatus(LatchStatus.LOCKED).build());

		this.mockMvc.perform(post(LOCKERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "status": "AVAILABLE", "limit": 10, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].status").value("AVAILABLE"));
	}

	@Test
	void search_whenFilterByRackId_shouldReturnFilteredResults() throws Exception {
		final RackEntity rack1 = this.createRack();
		final RackEntity rack2 = this.rackRepository
				.saveAndFlush(RackEntity.builder().number(2).status(RackStatus.ACTIVE).build());

		this.createLocker(rack1);
		this.lockerRepository.saveAndFlush(LockerEntity.builder().number(20).rack(rack2).status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED).build());

		this.mockMvc.perform(post(LOCKERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "rackId": "%s", "limit": 10, "offset": 0 }
				""".formatted(rack1.getId()))).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].number").value(10));
	}

	@Test
	void search_whenNoLockersExist_shouldReturnEmptyList() throws Exception {
		this.mockMvc.perform(post(LOCKERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 10, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void search_whenPaginationApplied_shouldReturnLimitedResults() throws Exception {
		final RackEntity rack = this.createRack();
		for (int i = 0; i < 5; i++) {
			this.lockerRepository.saveAndFlush(LockerEntity.builder().number(i).rack(rack)
					.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.CLOSED).build());
		}

		this.mockMvc.perform(post(LOCKERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 3, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
	}

	@Test
	void search_whenMissingPagination_shouldReturn400() throws Exception {
		this.mockMvc.perform(post(LOCKERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void changeStatus_whenLockerExists_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		this.mockMvc.perform(patch(LOCKERS_URL + "/{id}/status/{status}", locker.getId(), "MAINTENANCE"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(locker.getId().toString()))
				.andExpect(jsonPath("$.status").value("MAINTENANCE"))
				.andExpect(jsonPath("$.latchStatus").value("CLOSED"));

		final LockerEntity updated = this.lockerRepository.findById(locker.getId()).orElseThrow();
		assertThat(updated.getStatus()).isEqualTo(LockerStatus.MAINTENANCE);
	}

	@Test
	void changeStatus_whenLockerNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		this.mockMvc.perform(patch(LOCKERS_URL + "/{id}/status/{status}", randomId, "MAINTENANCE"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Locker with id '%s' not found".formatted(randomId)));
	}

	@Test
	void changeStatus_whenInvalidStatus_shouldReturn400() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		this.mockMvc.perform(patch(LOCKERS_URL + "/{id}/status/{status}", locker.getId(), "INVALID_STATUS"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void changeLatchStatus_whenLockerExists_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		this.mockMvc.perform(patch(LOCKERS_URL + "/{id}/latch/status/{status}", locker.getId(), "OPEN"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(locker.getId().toString()))
				.andExpect(jsonPath("$.latchStatus").value("OPEN")).andExpect(jsonPath("$.status").value("AVAILABLE"));

		final LockerEntity updated = this.lockerRepository.findById(locker.getId()).orElseThrow();
		assertThat(updated.getLatchStatus()).isEqualTo(LatchStatus.OPEN);
	}

	@Test
	void changeLatchStatus_whenLockerNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		this.mockMvc.perform(patch(LOCKERS_URL + "/{id}/latch/status/{status}", randomId, "OPEN"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Locker with id '%s' not found".formatted(randomId)));
	}

	@Test
	void changeLatchStatus_whenInvalidStatus_shouldReturn400() throws Exception {
		final RackEntity rack = this.createRack();
		final LockerEntity locker = this.createLocker(rack);

		this.mockMvc.perform(patch(LOCKERS_URL + "/{id}/latch/status/{status}", locker.getId(), "INVALID_LATCH"))
				.andExpect(status().isBadRequest());
	}
}
