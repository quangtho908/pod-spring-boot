package org.example.podbackend.modules.users.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.podbackend.common.enums.VerifyAction;

@Data
public class VerifyDTO {
  @NotNull
  @Size(min = 6, max = 6, message = "Code must be 6 digits")
  private String code;

  private VerifyAction verifyAction;
}
