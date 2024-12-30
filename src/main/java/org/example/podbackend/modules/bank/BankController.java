package org.example.podbackend.modules.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.podbackend.entities.Bank;
import org.example.podbackend.modules.bank.DTO.GenQRDTO;
import org.example.podbackend.modules.bank.DTO.GetAccountNameDTO;
import org.example.podbackend.modules.bank.response.AccountNameResponse;
import org.example.podbackend.modules.bank.response.BankResponse;
import org.example.podbackend.modules.bank.response.GenQRResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankController {
  private final BankService bankService;
  public BankController(BankService bankService) {
    this.bankService = bankService;
  }

  @GetMapping("/u") public ResponseEntity<List<BankResponse>> getBanks() {
    return this.bankService.getBanks();
  }

  @PostMapping("/u/genQr")
  public ResponseEntity<GenQRResponse> getBank(@RequestBody GenQRDTO dto) throws IOException, InterruptedException {
    return this.bankService.genQr(dto);
  }

  @GetMapping("/update")
  public ResponseEntity<Boolean> update() {
    return bankService.update();
  }

  @PostMapping()
  public ResponseEntity<AccountNameResponse> getAccountName(@RequestBody GetAccountNameDTO dto) {
    return bankService.getAccountName(dto);
  }
}
