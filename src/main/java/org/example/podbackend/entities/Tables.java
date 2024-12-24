package org.example.podbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tables")
public class Tables extends BaseEntity {

  @Column(nullable = false, name = "name")
  private String name;

  @Column(nullable = false, name = "is_used")
  private boolean isUsed = false;

  @Column(nullable = false, name = "is_deleted")
  private boolean isDeleted = false;

  @ManyToOne(optional = false)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;
}
