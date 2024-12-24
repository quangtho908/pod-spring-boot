package org.example.podbackend.modules.merchants;

import jakarta.validation.Valid;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.modules.merchants.DTO.CreateMerchantDTO;
import org.example.podbackend.modules.merchants.DTO.UpdateMerchantDTO;
import org.example.podbackend.modules.merchants.response.MerchantCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @PostMapping()
  public ResponseEntity<MerchantCreateResponse> createMerchant(@RequestBody @Valid CreateMerchantDTO dto) {
    return this.merchantService.create(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Boolean> updateMerchant( @PathVariable Long id, @RequestBody() UpdateMerchantDTO dto) {
    return this.merchantService.update(dto, id);
  }
}
