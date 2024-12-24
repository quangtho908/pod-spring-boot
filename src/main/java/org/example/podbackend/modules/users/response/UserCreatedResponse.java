package org.example.podbackend.modules.users.response;

import lombok.Data;

@Data
public class UserCreatedResponse {
  private long id;
  private String email;
  private String phoneNumber;
}
