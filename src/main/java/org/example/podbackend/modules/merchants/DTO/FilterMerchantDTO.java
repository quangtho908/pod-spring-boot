package org.example.podbackend.modules.merchants.DTO;

import lombok.Data;

@Data
public class FilterMerchantDTO {
  private Long id;
  private Integer limit = 10;
  private Integer page = 1;
}
