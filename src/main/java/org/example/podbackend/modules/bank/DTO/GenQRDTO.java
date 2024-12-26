package org.example.podbackend.modules.bank.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenQRDTO {
  @NotNull(message = "Bin not null")
  private String acqId;

  @NotNull(message = "Account number not null")
  private String accountNo;

  @NotNull(message = "Amount not null")
  private String amount;

  @NotNull(message = "Description not null")
  private String description;

  @NotNull(message = "Account name not null")
  private String accountName;

  private String template = "gmqFWPq";
}
