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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
		final ClassPathResource resource = new ClassPathResource("mocks/requests/users/create-user.json");
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
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON).content(
						new ClassPathResource("mocks/requests/users/create-user.json").getContentAsByteArray()))
				.andExpect(status().isCreated());

		final ClassPathResource resource = new ClassPathResource(
				"mocks/requests/users/create-user-duplicate-email.json");
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(resource.getContentAsByteArray()))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.code").value(409))
				.andExpect(jsonPath("$.title").value("Conflict"))
				.andExpect(jsonPath("$.detail").value("User with email 'john@example.com' already exists"));

		assertThat(this.userRepository.count()).isEqualTo(1);
	}

	@Test
	void whenUserHasMissingAttributes_thenReturn400() throws Exception {
		final ClassPathResource resource = new ClassPathResource(
				"mocks/requests/users/create-user-missing-attributes.json");
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON)
						.content(resource.getContentAsByteArray()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.title").value("Error")).andExpect(jsonPath("$.detail").isEmpty());

		assertThat(this.userRepository.count()).isZero();
	}

	@Test
	void getById_whenUserExists_shouldReturn200() throws Exception {
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON).content(
						new ClassPathResource("mocks/requests/users/create-user.json").getContentAsByteArray()))
				.andExpect(status().isCreated());

		final UserEntity persisted = this.userRepository.findAll().getFirst();

		this.mockMvc.perform(get(USERS_URL + "/{id}", persisted.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(persisted.getId().toString()))
				.andExpect(jsonPath("$.name").value("John Doe"))
				.andExpect(jsonPath("$.email").value("john@example.com"))
				.andExpect(jsonPath("$.phone").value("+123456789"));
	}

	@Test
	void getById_whenUserNotExists_shouldReturn404() throws Exception {
		final UUID randomId = UUID.randomUUID();

		this.mockMvc.perform(get(USERS_URL + "/{id}", randomId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value(404)).andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("User with id '%s' not found".formatted(randomId)));
	}

	@Test
	void search_whenUsersExist_shouldReturnList() throws Exception {
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON).content(
						new ClassPathResource("mocks/requests/users/create-user.json").getContentAsByteArray()))
				.andExpect(status().isCreated());

		this.mockMvc.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON).content("""
				{ "name": "Jane Smith", "email": "jane@example.com", "phone": "+987654321" }
				""")).andExpect(status().isCreated());

		assertThat(this.userRepository.count()).isEqualTo(2);

		this.mockMvc.perform(post(USERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 10, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").isNotEmpty()).andExpect(jsonPath("$[1].id").isNotEmpty());
	}

	@Test
	void search_whenNoUsersExist_shouldReturnEmptyList() throws Exception {
		this.mockMvc.perform(post(USERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 10, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void search_whenPaginationApplied_shouldReturnLimitedResults() throws Exception {
		for (int i = 0; i < 5; i++) {
			this.mockMvc.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON).content("""
					{ "name": "User %d", "email": "user%d@example.com", "phone": "+%d" }
					""".formatted(i, i, 1000000000 + i))).andExpect(status().isCreated());
		}

		assertThat(this.userRepository.count()).isEqualTo(5);

		this.mockMvc.perform(post(USERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 2, "offset": 0 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void search_whenOffsetExceedsTotal_shouldReturnEmptyList() throws Exception {
		this.mockMvc
				.perform(post(USERS_URL).contentType(MediaType.APPLICATION_JSON).content(
						new ClassPathResource("mocks/requests/users/create-user.json").getContentAsByteArray()))
				.andExpect(status().isCreated());

		this.mockMvc.perform(post(USERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("""
				{ "limit": 10, "offset": 100 }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void search_whenMissingPagination_shouldReturn400() throws Exception {
		this.mockMvc.perform(post(USERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void search_whenEmptyBody_shouldReturn400() throws Exception {
		this.mockMvc.perform(post(USERS_URL + "/search").contentType(MediaType.APPLICATION_JSON).content(""))
				.andExpect(status().isBadRequest());
	}
}
