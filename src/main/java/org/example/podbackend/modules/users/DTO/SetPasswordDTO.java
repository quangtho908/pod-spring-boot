package org.example.podbackend.modules.users.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetPasswordDTO {

  @NotNull(message = "Old password is required")
  @NotBlank(message = "Old password is required")
  @NotEmpty(message = "Old password is required")
  private String oldPassword;

  @NotNull(message = "Old password is required")
  @NotBlank(message = "Old password is required")
  @NotEmpty(message = "Old password is required")
  private String password;
}
