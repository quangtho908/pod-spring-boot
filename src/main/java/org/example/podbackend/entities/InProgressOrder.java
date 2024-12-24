package org.example.podbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.podbackend.common.enums.StatusOrder;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "in_progress_order")
public class InProgressOrder extends BaseEntity {
  @Enumerated(EnumType.STRING)
  private StatusOrder status = StatusOrder.WAITING;
  
  private String note;

  @Column(name = "is_take_out", nullable = false)
  private boolean isTakeOut = false;

  @OneToMany(mappedBy = "inProgressOrder")
  private List<ProductOrder> productOrders;

  @ManyToOne(optional = false)
  @JoinColumn(name = "table_id")
  private Tables tables;

  @ManyToOne(optional = false)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  @ManyToOne(optional = false)
  @JoinColumn(name = "created_by")
  private Users createdBy;
}
