package org.example.podbackend.modules.merchants.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.podbackend.common.models.BaseDTO;

@Data
public class UpdateMerchantDTO extends BaseDTO {
  @NotNull
  @NotEmpty
  private String address;

  @NotNull
  @NotEmpty
  private String name;
}
