package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateRackRequestDTO;
import com.ferdonof.locki.external.admin.dto.RackResponseDTO;
import com.ferdonof.locki.mappers.RackDtoMapper;
import com.ferdonof.locki.racks.usecases.CreateRack;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/racks")
public class RackController {

  private final RackDtoMapper rackDtoMapper;

  private final CreateRack createRack;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public RackResponseDTO createRack(CreateRackRequestDTO request) {
    final var rack = this.rackDtoMapper.toDomain(request);
    return this.rackDtoMapper.toDto(this.createRack.execute(rack));
  }
}
