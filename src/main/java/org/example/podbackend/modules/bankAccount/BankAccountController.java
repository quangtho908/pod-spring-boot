package org.example.podbackend.modules.bankAccount;

import org.example.podbackend.entities.BankAccount;
import org.example.podbackend.modules.bankAccount.DTO.CreateBankAccountDTO;
import org.example.podbackend.modules.bankAccount.response.BankAccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/bankAccounts")
@RestController
public class BankAccountController {
  private final BankAccountService bankAccountService;
  public BankAccountController(BankAccountService bankAccountService) {
    this.bankAccountService = bankAccountService;
  }

  @GetMapping()
  public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts(@RequestParam(name = "merchantId") Long merchantId) {
    return this.bankAccountService.getAll(merchantId);
  }

  @PostMapping()
  public ResponseEntity<BankAccountResponse> createBankAccount(@RequestBody CreateBankAccountDTO dto) {
    return this.bankAccountService.create(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Boolean> setDefault(@PathVariable Long id, @RequestParam Long merchantId) {
    return this.bankAccountService.setDefault(id, merchantId);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Boolean> delete(@PathVariable Long id, @RequestParam(name = "merchantId") Long merchantId) {
    return this.bankAccountService.delete(id, merchantId);
  }
}
