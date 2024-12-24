package org.example.podbackend.modules.authentication.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
  @NotNull
  @NotBlank
  @NotEmpty
  @Size(min = 10, max = 12, message = "Phone number must be 10 digits")
  private String phoneNumber;

  @NotNull
  @NotBlank
  @NotEmpty
  private String password;
}
