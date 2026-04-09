package com.ferdonof.locki.reservations.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ferdonof.locki.lockers.enums.LockerSize;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.enums.RackStatus;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "racked_lockers")
public class RackedLockerEntity implements Serializable {
  @Serial private static final long serialVersionUID = 6102100591941612221L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull
  private UUID lockerId;

  @NotNull
  private UUID rackId;

  @NotNull
  private int position;

  @NotNull
  @Enumerated(EnumType.STRING)
  private RackStatus rackStatus;

  @NotEmpty
  private String address;

  @NotEmpty
  private String city;

  @NotEmpty
  private String country;

  @NotEmpty
  private String zipCode;

  @NotNull
  @Column(precision = 12, scale = 6)
  private BigDecimal lat;

  @NotNull
  @Column(precision = 12, scale = 6)
  private BigDecimal lon;

  @NotNull
  @Enumerated(EnumType.STRING)
  private LockerStatus lockerStatus;

  @NotNull
  @Enumerated(EnumType.STRING)
  private LockerSize size;

  @Version
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false, updatable = true)
  private Instant updatedAt;
}
