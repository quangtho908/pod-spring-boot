package org.example.podbackend.modules.bankAccount.response;

import lombok.Data;

@Data
public class BankAccountResponse {
  private Long id;
  private String bankBin;
  private String accountName;
  private String accountNumber;
  private boolean isPrimary;
}
