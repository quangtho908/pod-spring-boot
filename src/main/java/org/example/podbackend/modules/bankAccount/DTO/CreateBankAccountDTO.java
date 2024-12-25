package org.example.podbackend.modules.bankAccount.DTO;

import lombok.Data;

@Data
public class CreateBankAccountDTO {
  private String bankBin;
  private String accountName;
  private String accountNumber;
  private Long merchantId;
}
