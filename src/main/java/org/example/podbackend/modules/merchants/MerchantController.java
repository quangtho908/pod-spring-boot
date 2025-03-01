package org.example.podbackend.modules.merchants;

import jakarta.validation.Valid;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.modules.merchants.DTO.*;
import org.example.podbackend.modules.merchants.response.MerchantCreateResponse;
import org.example.podbackend.modules.merchants.response.UserMerchantResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

  private final MerchantService merchantService;
  public MerchantController(MerchantService merchantService) {
    this.merchantService = merchantService;
  }

  @GetMapping()
  public ResponseEntity<?> filterMerchants(@RequestParam Map<String, String> allParam) {
    return this.merchantService.filter(allParam);
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<List<UserMerchantResponse>> getUsers(@PathVariable Long id) {
    return this.merchantService.users(id);
  }

  @PostMapping()
  public ResponseEntity<MerchantCreateResponse> createMerchant(@RequestBody @Valid CreateMerchantDTO dto) {
    return this.merchantService.create(dto);
  }

  @PutMapping("/avatar/{id}")
  public ResponseEntity<Boolean> uploadAvatar(@PathVariable Long id ,@ModelAttribute UploadAvatarDTO dto) throws IOException {
    return this.merchantService.updateAvatar(dto, id);
  }

  @PostMapping("/invite")
  public ResponseEntity<Boolean> inviteUser(@RequestBody @Valid InviteUserDTO dto) {
    return this.merchantService.invite(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Boolean> updateMerchant( @PathVariable Long id, @RequestBody() @Valid UpdateMerchantDTO dto) {
    return this.merchantService.update(dto, id);
  }

  @PutMapping("/users")
  public ResponseEntity<Boolean> deleteUsers(@RequestBody DeleteUserDTO dto) {
    return this.merchantService.deleteUser(dto);
  }
}
