package org.example.podbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.example.podbackend.Security.Models.PayloadToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

@Component
public class JWTService {
  private final SecretKey jwtSecret;
  private final MacAlgorithm algorithm;
  private final ObjectMapper objectMapper;

  public JWTService(ObjectMapper objectMapper) throws NoSuchAlgorithmException {
    this.algorithm = Jwts.SIG.HS256;
    String secret = "8Fmbh0OwrX2Oqyvlxsr22N0emzuuTy8c";
    this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes());
    this.objectMapper = objectMapper;
  }

  public String generateJwtToken(UserDetails payload) throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    return Jwts.builder()
            .claims(om.convertValue(payload, new TypeReference<Map<String, ?>>() {}))
            .expiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(jwtSecret, algorithm)
            .compact();
  }

  public PayloadToken getPayloadFromJwtToken(String token) {
    Claims claims = Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token).getPayload();
    return this.objectMapper.convertValue(claims, PayloadToken.class);
  }

  public boolean validateJwtToken(String token) {
    try {
      Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
