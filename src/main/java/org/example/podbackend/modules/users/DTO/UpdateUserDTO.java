package org.example.podbackend.modules.users.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserDTO {
  @NotNull
  private String fullName;
}
