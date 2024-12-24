package org.example.podbackend.common.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.podbackend.Security.Models.PayloadToken;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.entities.Users;
import org.example.podbackend.repositories.UserRepository;
import org.example.podbackend.utils.JWTService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JWTService jwtService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(
          JWTService jwtService,
          UserRepository userRepository,
          ObjectMapper objectMapper
  ) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
  }
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = request.getHeader("Authorization");
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }
    token = token.split(" ")[1];
    boolean tokenValid = this.jwtService.validateJwtToken(token);
    if(!tokenValid) {
      filterChain.doFilter(request, response);
      return;
    }

    PayloadToken payload = this.jwtService.getPayloadFromJwtToken(token);
    Optional<Users> exist = this.userRepository.findByPhoneNumber(payload.getUsername());

    if(exist.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getUsernamePasswordAuthenticationToken(exist.get(), payload);
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
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
