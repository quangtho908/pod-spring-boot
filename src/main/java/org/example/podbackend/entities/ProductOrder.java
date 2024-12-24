package org.example.podbackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_order")
public class ProductOrder extends BaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "in_progress_order_id")
  private InProgressOrder inProgressOrder;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  private int quantity;
}
