package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateRackRequestDTO;
import com.ferdonof.locki.external.admin.dto.LockerResponseDTO;
import com.ferdonof.locki.external.admin.dto.RackResponseDTO;
import com.ferdonof.locki.external.admin.dto.RacksFilterRequestDTO;
import com.ferdonof.locki.external.admin.dto.UpdateRackRequestDTO;
import com.ferdonof.locki.mappers.RackDtoMapper;
import com.ferdonof.locki.racks.usecases.CreateRack;
import com.ferdonof.locki.racks.usecases.GetRack;
import com.ferdonof.locki.racks.usecases.GetRackLockers;
import com.ferdonof.locki.racks.usecases.SearchRacks;
import com.ferdonof.locki.racks.usecases.UpdateRack;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/racks")
public class RackController {

  private final RackDtoMapper rackDtoMapper;

  private final CreateRack createRack;

  private final GetRack getRack;

  private final UpdateRack updateRack;

  private final SearchRacks searchRacks;

  private final GetRackLockers getRackLockers;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public RackResponseDTO createRack(@RequestBody @Valid CreateRackRequestDTO request) {
    final var rack = this.rackDtoMapper.toDomain(request);
    return this.rackDtoMapper.toDto(this.createRack.execute(rack));
  }

  @GetMapping("/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RackResponseDTO getById(@PathVariable UUID id) {
    return this.rackDtoMapper.toDto(this.getRack.execute(id));
  }

  @PutMapping("/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public RackResponseDTO update(@PathVariable UUID id, @RequestBody @Valid UpdateRackRequestDTO request) {
    return this.rackDtoMapper.toDto(this.updateRack.execute(this.rackDtoMapper.toUpdateRequest(id, request)));
  }

  @PostMapping("/search")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<RackResponseDTO> search(@RequestBody @Valid RacksFilterRequestDTO request) {
    return this.searchRacks.execute(this.rackDtoMapper.toFilter(request))
        .stream()
        .map(this.rackDtoMapper::toDto)
        .toList();
  }

  @GetMapping("/{id}/lockers")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<LockerResponseDTO> getRackLockers(@PathVariable UUID id) {
    return this.getRackLockers.execute(id)
        .stream()
        .map(this.rackDtoMapper::toLockerDto)
        .toList();
  }
}
