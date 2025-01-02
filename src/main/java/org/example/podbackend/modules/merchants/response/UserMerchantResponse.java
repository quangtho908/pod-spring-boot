package org.example.podbackend.modules.merchants.response;

import lombok.Data;

@Data
public class UserMerchantResponse {
  private String id;
  private String phoneNumber;
  private String email;
  private String role;
}
