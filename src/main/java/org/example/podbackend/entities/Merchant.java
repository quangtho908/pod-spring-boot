package org.example.podbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "merchants")
public class Merchant extends BaseEntity {
  @Column(nullable = false, name = "name", unique = true)
  private String name;

  @Column(nullable = false, name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @OneToMany(mappedBy = "merchant")
  private List<UserMerchant> userMerchants;

  @OneToMany(mappedBy = "merchant")
  private List<Tables> tables;

  @OneToMany(mappedBy = "merchant")
  private List<Product> products;

  @OneToMany(mappedBy = "merchant")
  private List<InProgressOrder> inProgressOrders;

  @OneToMany(mappedBy = "merchant")
  private List<BankAccount> bankAccounts;
}
