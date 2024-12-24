package org.example.podbackend.modules.products.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilterProductDTO {
  private Long id;
  private String name = "";
  @NotNull(message = "Merchant id is required")
  private Long merchantId;
  private Integer page;
  private Integer size;
}
