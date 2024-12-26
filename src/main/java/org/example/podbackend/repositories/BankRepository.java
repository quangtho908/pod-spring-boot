package org.example.podbackend.repositories;

import org.example.podbackend.entities.Bank;

public interface BankRepository extends BaseRepository<Bank, Long> {
  Bank findByBin(String bin);
}
