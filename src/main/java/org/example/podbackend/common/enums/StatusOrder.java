package org.example.podbackend.common.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusOrder {
  @JsonProperty("waiting")
  WAITING,

  @JsonProperty("progress")
  PROGRESS,

  @JsonProperty("done")
  DONE,

  @JsonProperty("canceled")
  CANCELED
}
