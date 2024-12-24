package org.example.podbackend.Security.Models;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import java.util.Set;

@Data
public class PayloadToken {
  private long id;
  private String username;
  private Set<GrantedAuthority> authorities;
  public boolean accountNonExpired;
  public boolean accountNonLocked;
  public boolean credentialsNonExpired;
  public boolean enabled;
  public long exp;
  public String password;
}
