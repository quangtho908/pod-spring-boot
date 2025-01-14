package org.example.podbackend.modules.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.UserLogin;
import org.example.podbackend.entities.UserMerchant;
import org.example.podbackend.entities.Users;
import org.example.podbackend.modules.merchants.response.UserMerchantResponse;
import org.example.podbackend.modules.users.DTO.*;
import org.example.podbackend.modules.users.response.SetMerchantResponse;
import org.example.podbackend.modules.users.response.UserCreatedResponse;
import org.example.podbackend.modules.users.response.UserResponse;
import org.example.podbackend.repositories.UserLoginRepository;
import org.example.podbackend.repositories.UserMerchantRepository;
import org.example.podbackend.utils.CloudinaryService;
import org.example.podbackend.utils.MultiPartHandle;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UsersService usersService;

  public UserController(UsersService usersService) {
    this.usersService = usersService;
  }

  @GetMapping
  private ResponseEntity<UserResponse> getUser() {
    return this.usersService.getUser();
  }

  @PostMapping("/avatar")
  private ResponseEntity<Boolean> setAvatar(@ModelAttribute SetAvatarDTO dto) throws IOException {
    return this.usersService.setAvatar(dto);
  }

  @PostMapping()
  public ResponseEntity<UserCreatedResponse> create(@RequestBody @Valid CreateUserDTO dto) throws JsonProcessingException {
    return this.usersService.create(dto);
  }

  @PostMapping("/setMerchant")
  public ResponseEntity<SetMerchantResponse> setMerchant(@RequestBody @Valid SetMerchantDTO dto) throws JsonProcessingException {
    return this.usersService.setMerchant(dto);
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

  @PutMapping()
  public ResponseEntity<Boolean> updateUser(@RequestBody @Valid UpdateUserDTO dto)  {
    return this.usersService.updateUser(dto);
  }
}
