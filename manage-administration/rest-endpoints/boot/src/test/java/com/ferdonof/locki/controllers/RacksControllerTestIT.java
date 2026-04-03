package com.ferdonof.locki.controllers;

import com.ferdonof.locki.entities.LocationEntity;
import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.racks.enums.RackStatus;
import com.ferdonof.locki.repositories.LocationRepository;
import com.ferdonof.locki.repositories.LockerRepository;
import com.ferdonof.locki.repositories.RackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RacksControllerTestIT {

	private static final String RACKS_URL = "/admin/racks";

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RackRepository rackRepository;

	@Autowired
	private LockerRepository lockerRepository;

	@Autowired
	private LocationRepository locationRepository;

	@BeforeEach
	void setUp() {
		this.lockerRepository.deleteAll();
		this.rackRepository.deleteAll();
		this.locationRepository.deleteAll();
	}

	private LocationEntity createLocation() {
		return this.locationRepository.saveAndFlush(LocationEntity.builder()
				.code("MAD")
				.city("Madrid")
				.country("Spain")
				.latitude(BigDecimal.valueOf(40.4168))
				.longitude(BigDecimal.valueOf(-3.7038))
				.build());
	}

	private RackEntity createRack(LocationEntity location) {
		return this.rackRepository.saveAndFlush(RackEntity.builder()
				.number(1)
				.status(RackStatus.ACTIVE)
				.location(location)
				.build());
	}

	private RackEntity createRackWithoutLocation() {
		return this.rackRepository.saveAndFlush(RackEntity.builder()
				.number(1)
				.status(RackStatus.ACTIVE)
				.build());
	}

	@Test
	void create_whenValidRequest_shouldReturn201() throws Exception {
		this.mockMvc
				.perform(post(RACKS_URL).contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "number": 1, "status": "ACTIVE" }
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andExpect(jsonPath("$.location").isEmpty());

		assertThat(this.rackRepository.count()).isEqualTo(1);
	}

	@Test
	void create_whenValidRequestWithLocation_shouldReturn201() throws Exception {
		final LocationEntity location = this.createLocation();

		this.mockMvc
				.perform(post(RACKS_URL).contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "number": 1, "status": "ACTIVE", "locationId": "%s" }
								""".formatted(location.getId())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andExpect(jsonPath("$.location").isNotEmpty())
				.andExpect(jsonPath("$.location.city").value("Madrid"));

		assertThat(this.rackRepository.count()).isEqualTo(1);
	}

	@Test
	void create_whenLocationDoesNotExist_shouldReturn201WithNullLocation() throws Exception {
		this.mockMvc
				.perform(post(RACKS_URL).contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "number": 1, "status": "ACTIVE", "locationId": "%s" }
								""".formatted(UUID.randomUUID())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.location").isEmpty());

		assertThat(this.rackRepository.count()).isEqualTo(1);
	}

	@Test
	void create_whenMissingRequiredAttributes_shouldReturn400() throws Exception {
		this.mockMvc
				.perform(post(RACKS_URL).contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());

		assertThat(this.rackRepository.count()).isZero();
	}

	@Test
	void getById_whenRackExists_shouldReturn200() throws Exception {
		final LocationEntity location = this.createLocation();
		final RackEntity rack = this.createRack(location);

		this.mockMvc
				.perform(get(RACKS_URL + "/{id}", rack.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(rack.getId().toString()))
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andExpect(jsonPath("$.location.city").value("Madrid"));
	}

	@Test
	void getById_whenRackExistsWithoutLocation_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRackWithoutLocation();

		this.mockMvc
				.perform(get(RACKS_URL + "/{id}", rack.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(rack.getId().toString()))
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.status").value("ACTIVE"))
				.andExpect(jsonPath("$.location").isEmpty());
	}

	@Test
	void getById_whenRackNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		this.mockMvc
				.perform(get(RACKS_URL + "/{id}", randomId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Rack with id '%s' not found".formatted(randomId)));
	}

	@Test
	void update_whenValidRequest_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRackWithoutLocation();

		final String requestBody = """
				{
				  "id": "%s",
				  "status": "MAINTENANCE"
				}
				""".formatted(rack.getId());

		this.mockMvc
				.perform(put(RACKS_URL + "/{id}", rack.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(rack.getId().toString()))
				.andExpect(jsonPath("$.status").value("MAINTENANCE"));

		final RackEntity updated = this.rackRepository.findById(rack.getId()).orElseThrow();
		assertThat(updated.getStatus()).isEqualTo(RackStatus.MAINTENANCE);
	}

	@Test
	void update_whenRackNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		final String requestBody = """
				{
				  "id": "%s",
				  "status": "MAINTENANCE"
				}
				""".formatted(randomId);

		this.mockMvc
				.perform(put(RACKS_URL + "/{id}", randomId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Rack with id '%s' not found".formatted(randomId)));
	}

	@Test
	void update_whenMissingRequiredFields_shouldReturn400() throws Exception {
		final RackEntity rack = this.createRackWithoutLocation();

		this.mockMvc
				.perform(put(RACKS_URL + "/{id}", rack.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void update_whenChangingStatusToDecommissioned_shouldReturn200() throws Exception {
		final RackEntity rack = this.createRackWithoutLocation();

		final String requestBody = """
				{
				  "id": "%s",
				  "status": "DECOMMISSIONED"
				}
				""".formatted(rack.getId());

		this.mockMvc
				.perform(put(RACKS_URL + "/{id}", rack.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("DECOMMISSIONED"));
	}


	@Test
	void search_whenRacksExist_shouldReturnList() throws Exception {
		this.createRackWithoutLocation();
		this.rackRepository.saveAndFlush(RackEntity.builder()
				.number(2)
				.status(RackStatus.INACTIVE)
				.build());

		this.mockMvc
				.perform(post(RACKS_URL + "/search").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "limit": 10, "offset": 0 }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void search_whenNoRacksExist_shouldReturnEmptyList() throws Exception {
		this.mockMvc
				.perform(post(RACKS_URL + "/search").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "limit": 10, "offset": 0 }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void search_whenPaginationApplied_shouldReturnLimitedResults() throws Exception {
		for (int i = 0; i < 5; i++) {
			this.rackRepository.saveAndFlush(RackEntity.builder()
					.number(i)
					.status(RackStatus.ACTIVE)
					.build());
		}

		this.mockMvc
				.perform(post(RACKS_URL + "/search").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "limit": 3, "offset": 0 }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)));
	}

	@Test
	void search_whenOffsetExceedsTotal_shouldReturnEmptyList() throws Exception {
		this.createRackWithoutLocation();

		this.mockMvc
				.perform(post(RACKS_URL + "/search").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{ "limit": 10, "offset": 100 }
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void search_whenMissingPagination_shouldReturn400() throws Exception {
		this.mockMvc
				.perform(post(RACKS_URL + "/search").contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}


	@Test
	void getRackLockers_whenRackHasLockers_shouldReturnList() throws Exception {
		final RackEntity rack = this.createRackWithoutLocation();

		this.lockerRepository.saveAndFlush(LockerEntity.builder()
				.number(1)
				.rack(rack)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.build());
		this.lockerRepository.saveAndFlush(LockerEntity.builder()
				.number(2)
				.rack(rack)
				.status(LockerStatus.RESERVED)
				.latchStatus(LatchStatus.LOCKED)
				.build());

		this.mockMvc
				.perform(get(RACKS_URL + "/{id}/lockers", rack.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").isNotEmpty())
				.andExpect(jsonPath("$[1].id").isNotEmpty());
	}

	@Test
	void getRackLockers_whenRackHasNoLockers_shouldReturnEmptyList() throws Exception {
		final RackEntity rack = this.createRackWithoutLocation();

		this.mockMvc
				.perform(get(RACKS_URL + "/{id}/lockers", rack.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void getRackLockers_whenRackNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		this.mockMvc
				.perform(get(RACKS_URL + "/{id}/lockers", randomId))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Rack with id '%s' not found".formatted(randomId)));
	}
}

