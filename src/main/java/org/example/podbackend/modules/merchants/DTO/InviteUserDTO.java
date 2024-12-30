package org.example.podbackend.modules.merchants.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteUserDTO {
  @NotNull
  private String phoneNumber;

  @NotNull
  private Long merchantId;
}
