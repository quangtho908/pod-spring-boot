package org.example.podbackend.modules.authentication.response;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public class LoginResponse {
  private String token;

  public LoginResponse(String token) {
    this.token = token;
  }
}
