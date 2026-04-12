package com.ferdonof.locki.reservations.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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

import com.ferdonof.locki.reservations.enums.ReservationStatus;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservations")
public class ReservationEntity implements Serializable {
  @Serial private static final long serialVersionUID = -5323737365571829359L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Locker ID is required")
  private UUID lockerId;

  @NotNull(message = "Rack ID is required")
  private UUID rackId;

  @Min(1)
  private int position;

  @ManyToOne(fetch = FetchType.EAGER)
  private LocationEntity location;

  @NotEmpty(message = "Currency is required")
  private String currency;

  @NotNull(message = "Price is required")
  @DecimalMin("0.00")
  private BigDecimal price;

  @NotNull(message = "Reservation status is required")
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @NotNull(message = "Start date is required")
  private Instant startDate;

  @NotNull(message = "End date is required")
  private Instant endDate;

  @Version
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false, updatable = true)
  private Instant updatedAt;
}
