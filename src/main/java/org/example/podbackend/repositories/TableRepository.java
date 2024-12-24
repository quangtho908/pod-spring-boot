package org.example.podbackend.repositories;

import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.Tables;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TableRepository extends BaseRepository<Tables, Long>{
  public Tables findByIdAndMerchantIdAndIsDeletedIsFalse(Long id, Long merchantId);
  Tables findByIdAndMerchantIdAndIsUsedIsFalseAndIsDeletedIsFalse(long id, long merchantId);
  @Query("SELECT t FROM Tables t WHERE t.name LIKE %?1% AND t.merchant = ?2 AND t.isDeleted = false")
  public List<Tables> filter(String name, Merchant merchant);
}