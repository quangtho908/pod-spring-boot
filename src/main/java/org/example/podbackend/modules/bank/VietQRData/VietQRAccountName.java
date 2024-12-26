package org.example.podbackend.modules.bank.VietQRData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.podbackend.modules.bank.response.AccountNameResponse;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VietQRAccountName {
  private String code;
  private String desc;
  private AccountNameResponse data;
}
