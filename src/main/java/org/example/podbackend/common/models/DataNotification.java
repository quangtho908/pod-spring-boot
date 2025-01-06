package org.example.podbackend.common.models;

import lombok.Data;
import org.example.podbackend.common.enums.TypeNoti;

@Data
public class DataNotification {
  private TypeNoti type;
  public DataNotification(TypeNoti type) {
    this.type = type;
  }
}
