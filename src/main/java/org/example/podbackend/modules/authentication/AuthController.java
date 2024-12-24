package org.example.podbackend.modules.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.example.podbackend.modules.authentication.DTO.LoginDTO;
import org.example.podbackend.modules.authentication.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginDTO loginDTO) throws JsonProcessingException {
    return this.authService.login(loginDTO);
  }
}