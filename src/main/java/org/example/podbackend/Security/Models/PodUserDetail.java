package org.example.podbackend.Security.Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PodUserDetail extends User {
  @Serial
  private static final long serialVersionUID = 620L;
  private static final Log logger = LogFactory.getLog(User.class);
  private final long id;
  private String password;
  private final String username;
  private final Set<GrantedAuthority> authorities;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;

  public PodUserDetail(long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = Set.copyOf(authorities);
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.enabled = true;
  }

  public PodUserDetail(
          long id,
          String username,
          String password,
          Set<GrantedAuthority> authorities,
          boolean accountNonExpired,
          boolean accountNonLocked,
          boolean credentialsNonExpired,
          boolean enabled
  ) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities );
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
  }
}
