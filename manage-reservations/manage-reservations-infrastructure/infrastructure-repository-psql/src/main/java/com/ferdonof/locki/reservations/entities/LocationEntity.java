package com.ferdonof.locki.reservations.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "locations")
public class LocationEntity implements Serializable {
  @Serial private static final long serialVersionUID = -8807848242379527622L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "locationId cannot be null")
  private UUID locationId;

  @NotEmpty(message = "address cannot be empty")
  private String address;

  @NotEmpty(message = "city cannot be empty")
  private String city;

  @NotEmpty(message = "country cannot be empty")
  private String country;

  @NotEmpty(message = "zipCode cannot be empty")
  private String zipCode;

  @NotNull(message = "lat cannot be null")
  @Column(precision = 12, scale = 6)
  private BigDecimal lat;

  @NotNull(message = "lon cannot be null")
  @Column(precision = 12, scale = 6)
  private BigDecimal lon;

  @Version
  private Long version;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false, updatable = true)
  private Instant updatedAt;
}
