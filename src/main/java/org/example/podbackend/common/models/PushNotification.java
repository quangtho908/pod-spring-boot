package org.example.podbackend.common.models;

import lombok.Data;

import java.util.List;

@Data
public class PushNotification {
  private String title;
  private String body;
  private List<String> to;
}
