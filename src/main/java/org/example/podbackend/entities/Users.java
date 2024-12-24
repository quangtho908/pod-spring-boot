package org.example.podbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "users")
public class Users extends BaseEntity{
  @Column(nullable = false, name = "full_name")
  private String fullName;

  @Column(unique = true, nullable = false)
  private String email;

  @Column()
  private String password;

  @Column(unique = true, nullable = false, name = "phone_number")
  private String phoneNumber;

  @Column(nullable = false, name = "is_active")
  private boolean isActive = false;

  @OneToMany(mappedBy = "user")
  private List<UserMerchant> userMerchants;

  @OneToMany(mappedBy = "createdBy")
  private List<InProgressOrder> inProgressOrders;
}
