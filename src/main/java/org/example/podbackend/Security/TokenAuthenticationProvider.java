package org.example.podbackend.Security;

import io.jsonwebtoken.Jwt;
import org.example.podbackend.Security.Models.PayloadToken;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.entities.Users;
import org.example.podbackend.modules.users.UsersService;
import org.example.podbackend.repositories.UserRepository;
import org.example.podbackend.utils.JWTService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {
  private final JWTService jwtService;
  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;
  public TokenAuthenticationProvider(JWTService jwtService, RedisTemplate<String, String> redisTemplate, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.redisTemplate = redisTemplate;
    this.userRepository = userRepository;
  }
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String refreshToken = authentication.getPrincipal().toString();
    boolean isValidToken = jwtService.validateRefreshToken(refreshToken);
    if (!isValidToken) {
      return null;
    }
    String existRefreshToken = redisTemplate.opsForValue().get(refreshToken);
    if (existRefreshToken == null || refreshToken.isEmpty()) {
      return null;
    }
    PayloadToken payload = jwtService.getPayloadFromRefreshToken(refreshToken);
    Optional<Users> exist = userRepository.findByPhoneNumber(payload.getUsername());
    if(exist.isEmpty()) {
      return null;
    }
    return getUsernamePasswordAuthenticationToken(exist.get(), payload);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return true;
  }

  private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(Users user, PayloadToken payload) {
    UserDetails userDetails = new PodUserDetail(
            user.getId(),
            payload.getUsername(),
            user.getPassword(),
            payload.getAuthorities(),
            payload.isAccountNonExpired(),
            payload.isCredentialsNonExpired(),
            payload.isAccountNonLocked(),
            payload.isEnabled()
    );
    return new UsernamePasswordAuthenticationToken(
            userDetails, null, payload.getAuthorities()
    );
  }
}
