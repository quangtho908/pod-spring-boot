package org.example.podbackend.modules.merchants.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteUserDTO {
  @NotNull
  private Long userId;

  @NotNull
  private Long merchantId;
}
