package org.example.podbackend.modules.bankAccount;

import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.common.mapper.BankAccountMapper;
import org.example.podbackend.entities.*;
import org.example.podbackend.modules.bankAccount.DTO.CreateBankAccountDTO;
import org.example.podbackend.modules.bankAccount.response.BankAccountResponse;
import org.example.podbackend.repositories.BankAccountsRepository;
import org.example.podbackend.repositories.BankRepository;
import org.example.podbackend.repositories.MerchantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class BankAccountService {
  private final BankAccountsRepository bankAccountsRepository;
  private final ModelMapper modelMapper;
  private final MerchantRepository merchantRepository;
  private final BankRepository bankRepository;
  private final BankAccountMapper bankAccountMapper;

  public BankAccountService(
          BankAccountsRepository bankAccountsRepository,
          ModelMapper modelMapper,
          MerchantRepository merchantRepository,
          BankRepository bankRepository,
          BankAccountMapper bankAccountMapper) {
    this.bankAccountsRepository = bankAccountsRepository;
    this.modelMapper = modelMapper;
    this.merchantRepository = merchantRepository;
    this.bankRepository = bankRepository;
    this.bankAccountMapper = bankAccountMapper;
  }

  public ResponseEntity<List<BankAccountResponse>> getAll(Long merchantId) {
    if(merchantId == null) {
      throw new BadRequestException("Merchant Id cannot be null");
    }
    List<BankAccount> accounts = bankAccountsRepository.findByMerchantId(merchantId);
    List<BankAccountResponse> accountResponses = accounts.stream().map(bankAccountMapper::mapToResponse).toList();
    return ResponseEntity.ok(accountResponses);
  }

  public ResponseEntity<BankAccountResponse> create(CreateBankAccountDTO dto) {
    if(dto.getMerchantId() == null) {
      throw new BadRequestException("Merchant Id cannot be null");
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) authentication.getPrincipal();
    Merchant merchant = merchantRepository.findByIdAndUserId(dto.getMerchantId(), userDetail.getId());
    if (merchant == null) {
      throw new BadRequestException("Merchant not found");
    }
    Bank bank = this.bankRepository.findByBin(dto.getBankBin());
    if (bank == null) {
      throw new BadRequestException("Bank not found");
    }

    BankAccount bankAccount = modelMapper.map(dto, BankAccount.class);
    bankAccount.setBank(bank);
    bankAccount.setMerchant(merchant);
    List<BankAccount>  bankAccounts = bankAccountsRepository.findByMerchantId(merchant.getId());
    if(bankAccounts.isEmpty()) {
      bankAccount.setPrimary(true);
    }
    bankAccountsRepository.save(bankAccount);
    return ResponseEntity.ok(modelMapper.map(bankAccount, BankAccountResponse.class));
  }

  public ResponseEntity<Boolean> delete(Long id, Long merchantId) {
    BankAccount bankAccount = bankAccountsRepository.findByIdAndMerchantId(id, merchantId);
    if(bankAccount == null) throw new NotFoundException("Bank Account not found");
    bankAccountsRepository.delete(bankAccount);
    return ResponseEntity.ok(true);
  }

  public ResponseEntity<Boolean> setDefault(Long id, Long merchantId) {
    BankAccount bankAccount = bankAccountsRepository.findByIdAndMerchantId(id, merchantId);
    BankAccount product = this.bankAccountsRepository.findByMerchantIdAndIsPrimaryIsTrue(merchantId);
    if(product != null) {
      product.setPrimary(false);
      bankAccountsRepository.save(product);
    };

    if(bankAccount == null) throw new NotFoundException("Bank Account not found");
    bankAccount.setPrimary(true);
    bankAccountsRepository.save(bankAccount);
    return ResponseEntity.ok(true);
  }
}
