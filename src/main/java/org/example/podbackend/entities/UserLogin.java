package org.example.podbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserLogin extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "user_id")
  private Users user;

  @ManyToOne
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  @Column(name = "fcm")
  private String fcm;
}
