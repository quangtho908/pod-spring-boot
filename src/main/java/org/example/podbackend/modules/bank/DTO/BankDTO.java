package org.example.podbackend.modules.bank.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankDTO {
  private Long id;
  private String name;
  private String code;
  private String bin;
  private String logo;

  @JsonProperty(value = "shortName")
  private String shortName;

  @JsonProperty(value = "transferSupported")
  private Integer transferSupported;

  @JsonProperty(value = "lookupSupported")
  private Integer lookupSupported;
}
