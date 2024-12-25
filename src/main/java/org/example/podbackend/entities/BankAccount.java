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
public class BankAccount extends BaseEntity {
  @Column(nullable = false, name = "bank_bin")
  private String bankBin;

  @Column(nullable = false, name = "account_number")
  private String accountNumber;

  @Column(nullable = false, name = "account_name")
  private String accountName;

  @Column(nullable = false, name= "is_primary")
  private boolean isPrimary = false;

  @ManyToOne(optional = false)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;
}
