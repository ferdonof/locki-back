package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.RacksFilter;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ferdonof.locki.racks.enums.RackStatus.ACTIVE;
import static com.ferdonof.locki.racks.enums.RackStatus.INACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchRacksTest {

	@Mock
	private RackRepositoryPort rackRepository;

	@InjectMocks
	private SearchRacksImpl searchRacksImpl;

	@Test
	void execute_whenRacksExist_shouldReturnList() {
		final var rack1 = Rack.builder().id(UUID.randomUUID()).number(1).status(ACTIVE).createdAt(Instant.now())
				.build();

		final var rack2 = Rack.builder().id(UUID.randomUUID()).number(2).status(INACTIVE).createdAt(Instant.now())
				.build();

		final var filter = RacksFilter.builder().limit(10).offset(0).build();
		final var racks = List.of(rack1, rack2);

		when(this.rackRepository.search(any(RacksFilter.class))).thenReturn(racks);

		final var result = this.searchRacksImpl.execute(filter);

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result).containsExactlyInAnyOrder(rack1, rack2);

		verify(this.rackRepository).search(any(RacksFilter.class));
	}

	@Test
	void execute_whenNoRacksExist_shouldReturnEmptyList() {
		final var filter = RacksFilter.builder().limit(10).offset(0).build();

		when(this.rackRepository.search(any(RacksFilter.class))).thenReturn(List.of());

		final var result = this.searchRacksImpl.execute(filter);

		assertThat(result).isNotNull().isEmpty();

		verify(this.rackRepository).search(any(RacksFilter.class));
	}

	@Test
	void execute_withPagination_shouldReturnPaginatedResults() {
		final var racks = List.of(Rack.builder().id(UUID.randomUUID()).number(1).status(ACTIVE).build());

		final var filter = RacksFilter.builder().limit(1).offset(0).build();

		when(this.rackRepository.search(filter)).thenReturn(racks);

		final var result = this.searchRacksImpl.execute(filter);

		assertThat(result).hasSize(1);

		verify(this.rackRepository).search(filter);
	}
}
