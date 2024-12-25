package org.example.podbackend.repositories;

import org.example.podbackend.entities.BankAccount;

import java.util.List;

public interface BankAccountsRepository extends BaseRepository<BankAccount, Long> {
  List<BankAccount> findByMerchantId (long merchant_id);

  BankAccount findByIdAndMerchantId(long id, long merchant_id);

  BankAccount findByMerchantIdAndIsPrimaryIsTrue(long merchantId);
}
