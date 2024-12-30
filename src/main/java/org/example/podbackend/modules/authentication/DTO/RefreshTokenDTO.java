package org.example.podbackend.modules.authentication.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenDTO {
  @NotNull
  private String refreshToken;
}
