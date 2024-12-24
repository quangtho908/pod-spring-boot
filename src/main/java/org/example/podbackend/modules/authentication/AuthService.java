package org.example.podbackend.modules.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.podbackend.modules.authentication.DTO.LoginDTO;
import org.example.podbackend.modules.authentication.response.LoginResponse;
import org.example.podbackend.utils.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  public AuthService(AuthenticationManager authenticationManager, JWTService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public ResponseEntity<LoginResponse> login(LoginDTO loginDTO) throws JsonProcessingException {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
            loginDTO.getPhoneNumber(),
            loginDTO.getPassword()
    );
    Authentication result = authenticationManager.authenticate(authentication);
    SecurityContextHolder.getContext().setAuthentication(result);
    String token = jwtService.generateJwtToken((UserDetails) result.getPrincipal());
    return new ResponseEntity<>(new LoginResponse(token), HttpStatus.OK);
  }
}
