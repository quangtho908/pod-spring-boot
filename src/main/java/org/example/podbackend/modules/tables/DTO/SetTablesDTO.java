package org.example.podbackend.modules.tables.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetTablesDTO {
  @NotNull(message = "Table name cannot be empty")
  @NotEmpty(message = "Table name cannot be empty")
  @NotBlank(message = "Table name cannot be empty")
  private String name;

  @NotNull(message = "Merchant id cannot be null")
  private Long merchantId;
}
