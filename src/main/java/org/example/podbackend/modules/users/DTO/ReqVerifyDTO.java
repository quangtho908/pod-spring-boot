package org.example.podbackend.modules.users.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.podbackend.common.enums.VerifyAction;

@Data
public class ReqVerifyDTO {
  private VerifyAction verifyAction;
}
