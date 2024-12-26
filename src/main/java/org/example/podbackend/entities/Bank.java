package org.example.podbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Bank extends BaseEntity {
  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false, unique = true)
  private String bin;

  @Column(nullable = false)
  private String shortName;

  @Column(nullable = false)
  private String logo;

  @Column(nullable = false, name = "transfer_supported")
  private Integer transferSupported;

  @Column(nullable = false, name = "lookup_supported")
  private Integer lookupSupported;
}
