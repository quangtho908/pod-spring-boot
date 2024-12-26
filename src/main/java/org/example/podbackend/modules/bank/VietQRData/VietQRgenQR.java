package org.example.podbackend.modules.bank.VietQRData;

import lombok.Data;
import org.example.podbackend.modules.bank.response.GenQRResponse;

@Data
public class VietQRgenQR {
  private String code;
  private String desc;
  private GenQRResponse data;
}
