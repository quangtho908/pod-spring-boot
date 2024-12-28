package org.example.podbackend.entities;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
  private String name;
  private long price;
  private int quantity;
  private String image;

  @ManyToOne(optional = false)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  @OneToMany(mappedBy = "product")
  private List<ProductOrder> productOrders;
}
