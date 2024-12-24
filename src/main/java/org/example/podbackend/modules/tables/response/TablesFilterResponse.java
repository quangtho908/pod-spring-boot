package org.example.podbackend.modules.tables.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TablesFilterResponse {
  private long id;
  private String name;

  @JsonProperty("isUsed")
  private boolean isUsed;
}
