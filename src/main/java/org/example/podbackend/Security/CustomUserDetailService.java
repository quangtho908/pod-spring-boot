package org.example.podbackend.Security;

import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.entities.Users;
import org.example.podbackend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;
  public CustomUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Users> exist = this.userRepository.findByPhoneNumber(username);
    if(exist.isEmpty()) {
      throw new UsernameNotFoundException("User does not exist");
    }
    Users user = exist.get();
    return new PodUserDetail(user.getId(), username, user.getPassword(), Set.of());
  }
}
