package org.example.podbackend.modules.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.podbackend.entities.Users;
import org.example.podbackend.modules.users.DTO.CreateUserDTO;
import org.example.podbackend.modules.users.DTO.ReqVerifyDTO;
import org.example.podbackend.modules.users.DTO.SetPasswordDTO;
import org.example.podbackend.modules.users.DTO.VerifyDTO;
import org.example.podbackend.modules.users.response.UserCreatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UsersService usersService;

  public UserController(UsersService usersService) {
    this.usersService = usersService;
  }

  @PostMapping()
  public ResponseEntity<UserCreatedResponse> create(@RequestBody @Valid CreateUserDTO dto) throws JsonProcessingException {
    return this.usersService.create(dto);
  }

  @PostMapping("/setPassword")
  public ResponseEntity<Boolean> setPassword(@RequestBody SetPasswordDTO dto) throws JsonProcessingException {
    return this.usersService.setPassword(dto);
  }

  @PostMapping("/reqVerify")
  public ResponseEntity<Boolean> reqVerify(@RequestBody ReqVerifyDTO dto) {
    return this.usersService.reqVerify(dto);
  }

  @PostMapping("/verify")
  public ResponseEntity<Boolean> verify(@RequestBody VerifyDTO dto) {
    return this.usersService.verify(dto);
  }
}
