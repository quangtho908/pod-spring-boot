package org.example.podbackend.modules.bank.response;

import lombok.Data;

@Data
public class GenQRResponse {
  private String qrCode;
  private String qrDataURL;
}
