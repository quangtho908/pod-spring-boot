package org.example.podbackend.modules.orders.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductOrderDTO {

  @NotNull(message = "Product id is required")
  public Long productId;
  public int quantity;
}
