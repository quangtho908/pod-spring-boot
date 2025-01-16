package org.example.podbackend.repositories;

import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.UserMerchant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MerchantRepository extends BaseRepository<Merchant, Long> {
  public Merchant findByName(String name);

  @Query("SELECT m FROM Merchant m INNER JOIN m.userMerchants um ON um.merchant.id = :id AND um.user.id = :userId")
  public Merchant findByIdAndUserId(Long id, Long userId);

  @Query("SELECT m FROM Merchant m INNER JOIN m.userMerchants um ON um.merchant.id = m.id AND um.user.id = :id")
  public List<Merchant> filter(long id, Pageable pageable);

  Merchant findByIdAndIsActiveIsTrue(Long merchantId);
}
