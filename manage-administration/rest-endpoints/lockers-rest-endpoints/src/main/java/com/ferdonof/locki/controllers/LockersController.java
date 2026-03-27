package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateLockerRequestDTO;
import com.ferdonof.locki.external.admin.dto.LockerResponseDTO;
import com.ferdonof.locki.lockers.usecases.CreateLocker;
import com.ferdonof.locki.mappers.LockerDtoMapper;
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
@RequestMapping("/admin/lockers")
public class LockersController {

  private final LockerDtoMapper lockerDtoMapper;

  private final CreateLocker createLocker;

//  private final GetLocker getLocker;
//
//  private final SearchLockers searchLockers;


  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public LockerResponseDTO create(@RequestBody @Valid CreateLockerRequestDTO request) {
    return this.lockerDtoMapper.toDto(this.createLocker.execute(this.lockerDtoMapper.toDomain(request)));
  }

}
