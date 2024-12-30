package org.example.podbackend.modules.authentication.response;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public class LoginResponse {
  private String token;
  private String refreshToken;

  public LoginResponse(String token, String refreshToken) {
    this.token = token;
    this.refreshToken = refreshToken;
  }
}
