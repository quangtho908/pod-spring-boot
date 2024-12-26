package org.example.podbackend.common.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentMethod {
  @JsonProperty("bank")
  BANK,

  @JsonProperty("cash")
  CASH
}
