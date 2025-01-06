package org.example.podbackend.common.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeNoti {

  @JsonProperty("createOrder")
  CREATE_ORDER,
  @JsonProperty("paymentOrder")
  PAYMENT_ORDER
}
