package com.ferdonof.locki.entities;

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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ferdonof.locki.lockers.enums.LockerSize;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fees")
public class FeeEntity implements Serializable {
  @Serial private static final long serialVersionUID = 7555206700805752917L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private Long id;

  @NotNull(message = "Locker size is required")
  @Enumerated(EnumType.STRING)
  private LockerSize lockerSize;

  @NotEmpty(message = "Country is required")
  private String country;

  @NotEmpty(message = "Currency is required")
  private String currency;

  @NotNull(message = "Price is required")
  @Column(precision = 12, scale = 2)
  private BigDecimal price;

  @Version
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false, updatable = true)
  private Instant updatedAt;
}
