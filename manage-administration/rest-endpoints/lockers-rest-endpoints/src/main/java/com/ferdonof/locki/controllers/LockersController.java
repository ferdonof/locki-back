package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateLockerRequestDTO;
import com.ferdonof.locki.external.admin.dto.LockerResponseDTO;
import com.ferdonof.locki.external.admin.dto.LockersFilterRequestDTO;
import com.ferdonof.locki.external.admin.dto.UpdateLockerRequestDTO;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.usecases.ChangeLatchStatus;
import com.ferdonof.locki.lockers.usecases.ChangeLockerStatus;
import com.ferdonof.locki.lockers.usecases.CreateLocker;
import com.ferdonof.locki.lockers.usecases.GetLocker;
import com.ferdonof.locki.lockers.usecases.SearchLockers;
import com.ferdonof.locki.lockers.usecases.UpdateLocker;
import com.ferdonof.locki.mappers.LockerDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/admin/lockers")
public class LockersController {

  private final LockerDtoMapper lockerDtoMapper;

  private final CreateLocker createLocker;

  private final GetLocker getLocker;

  private final UpdateLocker updateLocker;

  private final SearchLockers searchLockers;

  private final ChangeLockerStatus changeLockerStatus;

  private final ChangeLatchStatus changeLatchStatus;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public LockerResponseDTO create(@RequestBody @Valid CreateLockerRequestDTO request) {
    return this.lockerDtoMapper.toDto(this.createLocker.execute(this.lockerDtoMapper.toDomain(request)));
  }

  @GetMapping("/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public LockerResponseDTO getById(@PathVariable UUID id) {
    return this.lockerDtoMapper.toDto(this.getLocker.execute(id));
  }

  @PutMapping("/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public LockerResponseDTO update(@PathVariable UUID id, @RequestBody @Valid UpdateLockerRequestDTO request) {
    return this.lockerDtoMapper.toDto(this.updateLocker.execute(this.lockerDtoMapper.toUpdateRequest(id, request)));
  }

  @PostMapping("/search")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<LockerResponseDTO> search(@RequestBody @Valid LockersFilterRequestDTO request) {
    return this.searchLockers.execute(this.lockerDtoMapper.toFilter(request))
        .stream()
        .map(this.lockerDtoMapper::toDto)
        .toList();
  }

  @PatchMapping("/{id}/status/{status}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public LockerResponseDTO changeStatus(@PathVariable UUID id, @PathVariable LockerStatus status) {
    return this.lockerDtoMapper.toDto(this.changeLockerStatus.execute(id, status));
  }

  @PatchMapping("/{id}/latch/status/{status}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public LockerResponseDTO changeLatchStatus(@PathVariable UUID id, @PathVariable LatchStatus status) {
    return this.lockerDtoMapper.toDto(this.changeLatchStatus.execute(id, status));
  }
}
