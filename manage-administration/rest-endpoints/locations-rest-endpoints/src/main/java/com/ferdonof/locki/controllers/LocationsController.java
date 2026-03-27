package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateLocationRequestDTO;
import com.ferdonof.locki.external.admin.dto.LocationResponseDTO;
import com.ferdonof.locki.locations.usecases.CreateLocation;
import com.ferdonof.locki.mappers.LocationDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/locations")
public class LocationsController {

  private final LocationDtoMapper locationDtoMapper;

  private final CreateLocation createLocation;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public LocationResponseDTO create(@Valid @RequestBody CreateLocationRequestDTO request) {
    return this.locationDtoMapper.toDto(this.createLocation.execute(this.locationDtoMapper.toDomain(request)));
  }

}
