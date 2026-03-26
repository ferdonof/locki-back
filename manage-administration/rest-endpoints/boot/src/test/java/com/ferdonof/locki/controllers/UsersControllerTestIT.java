package com.ferdonof.locki.controllers;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.repositories.UserRepository;
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
class UsersControllerTestIT {

	private static final String USERS_URL = "/admin/users";

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		this.userRepository.deleteAll();
	}

	@Test
	void whenCreateUserOk_thenReturn201() throws Exception {
		final ClassPathResource resource = new ClassPathResource("mocks/requests/create-user.json");
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(resource.getContentAsByteArray()))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.name").value("John Doe"))
				.andExpect(jsonPath("$.email").value("john@example.com"));

		assertThat(this.userRepository.count()).isEqualTo(1);

		final UserEntity persisted = this.userRepository.findAll().getFirst();
		assertThat(persisted.getName()).isEqualTo("John Doe");
		assertThat(persisted.getEmail()).isEqualTo("john@example.com");
		assertThat(persisted.getCreatedAt()).isNotNull();
	}

	@Test
	void whenUserAlreadyExists_thenReturn409() throws Exception {
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(new ClassPathResource("mocks/requests/create-user.json").getContentAsByteArray()))
				.andExpect(status().isCreated());

		final ClassPathResource resource = new ClassPathResource("mocks/requests/create-user-duplicate-email.json");
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(resource.getContentAsByteArray()))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.code").value(409))
				.andExpect(jsonPath("$.title").value("Conflict"))
				.andExpect(jsonPath("$.detail").value("User with email 'john@example.com' already exists"));

		assertThat(this.userRepository.count()).isEqualTo(1);
	}
}
