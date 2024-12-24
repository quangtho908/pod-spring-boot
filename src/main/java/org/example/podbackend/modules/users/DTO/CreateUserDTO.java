package org.example.podbackend.modules.users.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {
  @NotNull(message = "Full name is required")
  private String fullName;

  @NotNull(message = "Email is required")
  @Email(message = "Email is not valid")
  private String email;

  @NotNull(message = "Phone number is required")
  @Size(min = 10, max = 12, message = "Phone number must be 10 digits")
  private String phoneNumber;

  @NotNull(message = "Password is required")
  private String password;
}
