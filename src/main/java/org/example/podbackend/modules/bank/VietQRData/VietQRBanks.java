package org.example.podbackend.modules.bank.VietQRData;

import lombok.Data;
import org.example.podbackend.modules.bank.DTO.BankDTO;

import java.util.List;

@Data
public class VietQRBanks {
  private String code;
  private String desc;
  private List<BankDTO> data;
}
