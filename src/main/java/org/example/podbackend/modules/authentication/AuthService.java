package org.example.podbackend.modules.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.podbackend.Security.Models.PayloadToken;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.modules.authentication.DTO.LoginDTO;
import org.example.podbackend.modules.authentication.DTO.RefreshTokenDTO;
import org.example.podbackend.modules.authentication.response.LoginResponse;
import org.example.podbackend.modules.users.DTO.CreateUserDTO;
import org.example.podbackend.modules.users.UsersService;
import org.example.podbackend.utils.JWTService;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final UsersService usersService;
  private final ModelMapper modelMapper;
  private final RedisTemplate<String, String> redisTemplate;

  public AuthService(
          AuthenticationManager authenticationManager,
          JWTService jwtService,
          UsersService usersService,
          ModelMapper modelMapper,
          RedisTemplate<String, String> redisTemplate
  ) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.usersService = usersService;
    this.modelMapper = modelMapper;
    this.redisTemplate = redisTemplate;
  }

  public ResponseEntity<LoginResponse> login(LoginDTO loginDTO) throws JsonProcessingException {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
            loginDTO.getPhoneNumber(),
            loginDTO.getPassword()
    );
    Authentication result = authenticationManager.authenticate(authentication);
    PodUserDetail userDetails = (PodUserDetail) result.getPrincipal();
    SecurityContextHolder.getContext().setAuthentication(result);
    String token = jwtService.generateJwtToken(userDetails);
    String refreshToken = jwtService.generateJwtRefreshToken(userDetails);
    redisTemplate.opsForValue().set(refreshToken, String.valueOf(userDetails.getId()));
    return new ResponseEntity<>(new LoginResponse(token, refreshToken), HttpStatus.OK);
  }

  public ResponseEntity<LoginResponse> signup(CreateUserDTO dto) throws JsonProcessingException {
    LoginDTO loginDTO = modelMapper.map(dto, LoginDTO.class);
    this.usersService.create(dto);
    return this.login(loginDTO);
  }

  public ResponseEntity<LoginResponse> refresh(RefreshTokenDTO dto) throws JsonProcessingException {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
            dto.getRefreshToken(),
            ""
    );
    Authentication result = authenticationManager.authenticate(authentication);
    PodUserDetail userDetails = (PodUserDetail) result.getPrincipal();
    SecurityContextHolder.getContext().setAuthentication(result);
    String token = jwtService.generateJwtToken(userDetails);
    LoginResponse loginResponse = new LoginResponse(token, dto.getRefreshToken());
    return new ResponseEntity<>(loginResponse, HttpStatus.OK);
  }
}
