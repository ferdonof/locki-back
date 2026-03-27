package com.ferdonof.locki.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.ferdonof.locki.entities.LocationEntity;
import com.ferdonof.locki.repositories.LocationRepository;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class LocationsControllerTestIT {

	private static final String LOCATIONS_URL = "/admin/locations";

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LocationRepository locationRepository;

	@BeforeEach
	void setUp() {
		this.locationRepository.deleteAll();
	}

	@Test
	void create_whenValidRequest_shouldReturn201() throws Exception {
		final String requestBody = """
				{
				  "city": "Madrid",
				  "country": "Spain",
				  "code": "MAD",
				  "latitude": 40.4168,
				  "longitude": -3.7038
				}
				""";

		this.mockMvc
				.perform(post(LOCATIONS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.city").value("Madrid"))
				.andExpect(jsonPath("$.country").value("Spain"));

		assertThat(this.locationRepository.count()).isEqualTo(1);

		final LocationEntity persisted = this.locationRepository.findAll().getFirst();
		assertThat(persisted.getCode()).isEqualTo("MAD");
		assertThat(persisted.getCity()).isEqualTo("Madrid");
		assertThat(persisted.getCountry()).isEqualTo("Spain");
		assertThat(persisted.getCreatedAt()).isNotNull();
	}

	@Test
	void create_whenMissingRequiredAttributes_shouldReturn400() throws Exception {
		this.mockMvc
				.perform(post(LOCATIONS_URL).contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());

		assertThat(this.locationRepository.count()).isZero();
	}

	@Test
	void create_whenDataIntegrityViolation_shouldReturn400() throws Exception {
		final String requestBody = """
				{
				  "city": "Madrid",
				  "country": "Spain"
				}
				""";

		this.mockMvc
				.perform(post(LOCATIONS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isBadRequest());

		assertThat(this.locationRepository.count()).isZero();
	}
}

