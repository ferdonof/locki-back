package com.ferdonof.locki.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ferdonof.locki.external.admin.dto.CreateFeeRequestDTO;
import com.ferdonof.locki.external.admin.dto.FeeResponseDTO;
import com.ferdonof.locki.fees.usecases.CreateFee;
import com.ferdonof.locki.fees.usecases.DeleteFee;
import com.ferdonof.locki.fees.usecases.GetFeeList;
import com.ferdonof.locki.fees.usecases.UpdateFee;
import com.ferdonof.locki.mappers.FeeDtoMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/fees")
public class FeeController {

  private final FeeDtoMapper feeDtoMapper;

  private final CreateFee createFee;

  private final DeleteFee deleteFee;

  private final UpdateFee updateFee;

  private final GetFeeList getFeeList;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public FeeResponseDTO create(@RequestBody @Valid CreateFeeRequestDTO request) {
    final var fee = this.feeDtoMapper.toDomain(request);
    return this.feeDtoMapper.toDto(this.createFee.execute(fee));
  }

  @PutMapping("/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public FeeResponseDTO update(@PathVariable UUID id, @RequestBody @Valid CreateFeeRequestDTO request) {
    final var fee = this.feeDtoMapper.toUpdateRequest(id, request);
    return this.feeDtoMapper.toDto(this.updateFee.execute(fee));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    this.deleteFee.execute(id);
  }

  @GetMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<FeeResponseDTO> search(@RequestBody CreateFeeRequestDTO request) {
    final var fee = this.feeDtoMapper.toDomain(request);
    return this.getFeeList
        .execute(fee.id(), fee.country(), fee.currency())
        .stream()
        .map(this.feeDtoMapper::toDto)
        .toList();
  }
}
