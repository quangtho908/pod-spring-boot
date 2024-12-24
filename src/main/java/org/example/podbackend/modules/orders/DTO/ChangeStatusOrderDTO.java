package org.example.podbackend.modules.orders.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.podbackend.common.enums.StatusOrder;

@Data
public class ChangeStatusOrderDTO {

  @NotNull(message = "Merchant id is required")
  private Long merchantId;

  private StatusOrder status;
}
