package org.example.podbackend.modules.users.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.example.podbackend.common.enums.Roles;

@Data
public class SetMerchantResponse {

  @Enumerated(EnumType.STRING)
  private Roles role;
}
