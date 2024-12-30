package org.example.podbackend.modules.users.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetMerchantDTO {
  @NotNull
  private Long merchantId;

  @NotNull
  private String expoToken;
}
