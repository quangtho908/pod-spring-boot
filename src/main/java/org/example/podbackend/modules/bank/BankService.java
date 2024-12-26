package org.example.podbackend.modules.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.entities.Bank;
import org.example.podbackend.modules.bank.DTO.GenQRDTO;
import org.example.podbackend.modules.bank.DTO.GetAccountNameDTO;
import org.example.podbackend.modules.bank.VietQRData.VietQRAccountName;
import org.example.podbackend.modules.bank.VietQRData.VietQRBanks;
import org.example.podbackend.modules.bank.VietQRData.VietQRgenQR;
import org.example.podbackend.modules.bank.response.AccountNameResponse;
import org.example.podbackend.modules.bank.response.BankResponse;
import org.example.podbackend.modules.bank.response.GenQRResponse;
import org.example.podbackend.repositories.BankRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankService {
  private final BankRepository bankRepository;
  private final String api = "https://api.vietqr.io/v2";
  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;

  public BankService(BankRepository bankRepository, ModelMapper modelMapper, ObjectMapper objectMapper) {
    this.bankRepository = bankRepository;
    this.modelMapper = modelMapper;
    this.objectMapper = objectMapper;
  }

  public ResponseEntity<List<BankResponse>> getBanks() {
    List<Bank> banks = bankRepository.findAll();
    List<BankResponse> bankResponses = banks.stream().map(bank -> modelMapper.map(bank, BankResponse.class)).toList();
    return ResponseEntity.ok(bankResponses);
  }

  public ResponseEntity<AccountNameResponse> getAccountName(GetAccountNameDTO dto) {
    try {
      String jsonDto = new ObjectMapper().writeValueAsString(dto);
      HttpResponse<String> response = HttpClient.newHttpClient().send(
              getRequest("/lookup", "POST", HttpRequest.BodyPublishers.ofString(jsonDto)),
              HttpResponse.BodyHandlers.ofString()
      );
      VietQRAccountName accountName = objectMapper.readValue(response.body(), VietQRAccountName.class);
      return ResponseEntity.ok(accountName.getData());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public ResponseEntity<Boolean> update() {
    try {
      HttpResponse<String> response = HttpClient.newHttpClient().send(
              getRequest("/banks", "GET", null),
              HttpResponse.BodyHandlers.ofString()
      );
      VietQRBanks banksResponse = objectMapper.readValue(response.body(), VietQRBanks.class);
      List<Bank> banks = banksResponse.getData().stream().map(bankDTO -> modelMapper.map(bankDTO, Bank.class)).toList();
      this.bankRepository.saveAll(banks);
      return ResponseEntity.ok(true);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public ResponseEntity<GenQRResponse> genQr(GenQRDTO dto) throws IOException, InterruptedException {
    String json = objectMapper.writeValueAsString(dto);
    HttpResponse<String> response = HttpClient.newHttpClient().send(
            getRequest("/generate", "POST" , HttpRequest.BodyPublishers.ofString(json)),
            HttpResponse.BodyHandlers.ofString()
    );
    VietQRgenQR genQR = objectMapper.readValue(response.body(), VietQRgenQR.class);
    return ResponseEntity.ok(genQR.getData());
  }

  private HttpRequest getRequest(String path, String method, HttpRequest.BodyPublisher body) {
    if (body == null) {
      body = HttpRequest.BodyPublishers.noBody();
    }
    return HttpRequest.newBuilder()
            .uri(URI.create(api + path))
            .header("x-client-id", "5855e40d-a39e-406c-8c86-02ce3c2aa561")
            .header("x-api-key", "ef81bb90-ec5f-4b61-9928-188dce7c0258")
            .header("Content-Type", "application/json")
            .method(method, body)
            .build();
  }
}
