package org.example.podbackend.modules.orders.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveProductOrderDTO {
  @Valid
  @NotNull(groups = {ProductOrderDTO.class})
  private ProductOrderDTO[] products;
}
