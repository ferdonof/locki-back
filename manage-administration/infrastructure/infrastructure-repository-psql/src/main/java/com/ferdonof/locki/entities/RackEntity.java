package com.ferdonof.locki.entities;

import com.ferdonof.locki.racks.enums.RackStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "racks")
public class RackEntity implements Serializable {
  @Serial
  private static final long serialVersionUID = -4110956705692844010L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private int number;

  @Enumerated(EnumType.STRING)
  private RackStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  private LocationEntity location;

  @OneToMany(mappedBy = "rack", fetch = FetchType.LAZY)
  private List<LockerEntity> lockers;

  @Version
  private long version;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false, updatable = true)
  private Instant updatedAt;

}
