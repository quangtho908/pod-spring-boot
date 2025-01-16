package org.example.podbackend.modules.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.entities.Bank;
import org.example.podbackend.modules.bank.DTO.GenQRDTO;
import org.example.podbackend.modules.bank.DTO.GetAccountNameDTO;
import org.example.podbackend.modules.bank.VietQRData.LookUpAccountName;
import org.example.podbackend.modules.bank.VietQRData.VietQRBanks;
import org.example.podbackend.modules.bank.VietQRData.VietQRgenQR;
import org.example.podbackend.modules.bank.response.AccountNameResponse;
import org.example.podbackend.modules.bank.response.BankResponse;
import org.example.podbackend.modules.bank.response.GenQRResponse;
import org.example.podbackend.repositories.BankRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    List<String[]> apiKeys = new ArrayList<>();
    apiKeys.add(new String[] {"df56bd80-aadd-4f4c-a6db-848f56790383key", "f6ada704-74f1-4e96-bee8-050ea4fe903asecret"});
    apiKeys.add(new String[] {"7f7990ae-3559-4e82-acb3-fe422e05975dkey", "3fbd1658-3b0c-4da4-bf56-de125055aa63secret"});
    apiKeys.add(new String[] {"714cdd77-f061-4746-b9a4-7bb006ca1084key", "1669681f-a48b-447a-be73-3ebd765c3144secret"});
    apiKeys.add(new String[] {"d5fe3c67-0d45-4387-90cd-a852115e73bfkey", "f3f668d5-1d40-4b0f-a186-138ab0c386b4secret"});
    apiKeys.add(new String[] {"26c8750e-9b90-4358-9f99-a56880ae3803key", "68c64ace-58aa-499d-aaa4-f6538687b508secret"});
    apiKeys.add(new String[] {"419339a1-b79e-41b0-aa40-f9251ca2bdbdkey", "0e88a600-4c95-4f47-9a8b-bb5cc9c17409secret"});
    apiKeys.add(new String[] {"9c42ef4c-7eda-4732-9131-79118ec494d2key", "96079631-e44f-4753-b42d-7dacb5c2b721secret"});
    apiKeys.add(new String[] {"e81775ef-fa2e-4501-8f4d-d02aefd94104key", "5a5b63de-ae26-48fb-b082-b5c83d29c7e7secret"});
    apiKeys.add(new String[] {"ca4c8395-8a12-4e4d-9218-394e3642c185key", "2e4c6056-d743-47a0-aed5-204c65403f7bsecret"});
    apiKeys.add(new String[] {"afd320ec-b14a-4ff1-bd90-3486952e686ckey", "1f24f50a-d259-494b-a485-451806ca9a17secret"});
    apiKeys.add(new String[] {"44886eb0-c85f-411c-827d-a21edc75232dkey", "9a31373a-ec53-4467-b42e-4c25e0223379secret"});
    try {
      String jsonDto = new ObjectMapper().writeValueAsString(dto);
      HttpResponse<String> response = null;
      for (int i = 0; i < apiKeys.size(); i++) {
        response = HttpClient.newHttpClient().send(
                bankLookup(jsonDto, apiKeys.get(i)),
                HttpResponse.BodyHandlers.ofString()
        );
        if(response.statusCode() == 200) {
          break;
        }
        if(response.statusCode() == 422) {
          throw new BadRequestException("Account name not found");
        }
        if(response.statusCode() == 402 && i == apiKeys.size() - 1 ) {
          throw new BadRequestException("Account name not found");
        }
      }
      
      LookUpAccountName data = objectMapper.readValue(response.body(), LookUpAccountName.class);
      return ResponseEntity.ok(data.getData());
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

  private HttpRequest bankLookup(String json, String[] keys) throws JsonProcessingException {
    return HttpRequest.newBuilder()
            .uri(URI.create("https://api.banklookup.net/api/bank/id-lookup-prod"))
            .header("x-api-key", keys[0])
            .header("x-api-secret", keys[1])
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(json))
            .build();

  }
}
