package org.example.podbackend.modules.orders.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDTO {

  @NotNull(message = "Merchant id is required")
  private Long merchantId;

  private String note;

  private Long tableId;

  @Valid
  @NotNull(groups = {CreateOrderDTO.class})
  private List<ProductOrderDTO> products = List.of();

  @JsonProperty("isTakeOut")
  private boolean isTakeOut = false;
}
