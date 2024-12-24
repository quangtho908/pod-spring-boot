package org.example.podbackend.modules.tables.DTO;

import lombok.Data;

@Data
public class FilterTablesDTO {
  private Long id;
  private String name = "";
  private Long merchantId;
}
