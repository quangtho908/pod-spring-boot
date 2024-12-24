package org.example.podbackend.modules.orders.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderDTO {

  @NotNull(message = "Merchant id is required")
  private Long merchantId;

  private String note;

  @Valid
  @NotNull(groups = {UpdateOrderDTO.class})
  private List<ProductOrderDTO> products;
}
