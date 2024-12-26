package org.example.podbackend.modules.bank.DTO;

import lombok.Data;

@Data
public class GetAccountNameDTO {
  private String bin;
  private String accountNumber;
}
