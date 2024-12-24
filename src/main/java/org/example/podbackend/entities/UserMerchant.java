package org.example.podbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.podbackend.common.enums.Roles;

@Setter
@Getter
@Entity
@Table(name = "user_merchant")
public class UserMerchant extends BaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private Users user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Roles role = Roles.OWNER;
}
