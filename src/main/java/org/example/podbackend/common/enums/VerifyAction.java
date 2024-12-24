package org.example.podbackend.common.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VerifyAction {
  @JsonProperty("setPassword")
  SET_PASSWORD,

  @JsonProperty("createAccount")
  CREATE_ACCOUNT
}