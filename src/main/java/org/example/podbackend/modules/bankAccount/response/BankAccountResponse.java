package org.example.podbackend.modules.bankAccount.response;

import lombok.Data;
import org.example.podbackend.modules.bank.DTO.BankDTO;

@Data
public class BankAccountResponse {
  private Long id;
  private String accountName;
  private String accountNumber;
  private boolean isPrimary;
  private BankDTO bank;
}
