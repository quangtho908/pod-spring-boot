package org.example.podbackend.modules.bank.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankResponse {
  private Long id;
  private String name;
  private String code;
  private String bin;
  private String logo;

  private String shortName;

  @JsonProperty(value = "transferSupported")
  private Integer transferSupported;

  @JsonProperty(value = "lookupSupported")
  private Integer lookupSupported;
}
