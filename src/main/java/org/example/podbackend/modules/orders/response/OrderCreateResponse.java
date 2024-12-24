package org.example.podbackend.modules.orders.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.example.podbackend.common.enums.StatusOrder;

@Data
public class OrderCreateResponse {
  private long id;

  @Enumerated(EnumType.STRING)
  private StatusOrder status;
  private String note;
}
