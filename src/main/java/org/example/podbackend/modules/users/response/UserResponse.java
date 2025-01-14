package org.example.podbackend.modules.users.response;

import lombok.Data;

@Data
public class UserResponse {
  private String id;
  private String phoneNumber;
  private String email;
  private String fullName;
  private String avatar;
}
