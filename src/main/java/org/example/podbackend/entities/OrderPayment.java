package org.example.podbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.podbackend.common.enums.PaymentMethod;

@Setter
@Getter
@Entity
public class OrderPayment extends BaseEntity {
  @Column(name = "payment_method")
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod = PaymentMethod.CASH;

  private long price;

  @OneToOne()
  @JoinColumn(name = "in_progress_order")
  private InProgressOrder inProgressOrder;

  @ManyToOne()
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  private String image;
}
