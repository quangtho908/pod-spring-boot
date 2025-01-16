package org.example.podbackend.modules.bank.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetAccountNameDTO {
  @NotNull
  @NotEmpty
  private String bank;

  @NotNull
  @NotEmpty
  private String account;
}
