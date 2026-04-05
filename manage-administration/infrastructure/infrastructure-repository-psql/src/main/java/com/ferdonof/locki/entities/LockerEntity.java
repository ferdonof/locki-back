package com.ferdonof.locki.entities;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerSize;
import com.ferdonof.locki.lockers.enums.LockerStatus;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lockers")
public class LockerEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = -2663306887449168838L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private int serial;

  @ManyToOne(fetch = FetchType.LAZY)
  private RackEntity rack;

  @Enumerated(EnumType.STRING)
  private LockerStatus status;

  @Enumerated(EnumType.STRING)
  private LatchStatus latchStatus;

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
