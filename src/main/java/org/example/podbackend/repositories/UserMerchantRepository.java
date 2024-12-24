package org.example.podbackend.repositories;

import org.example.podbackend.entities.UserMerchant;

public interface UserMerchantRepository extends BaseRepository<UserMerchant, Long> {
  public UserMerchant findByUserIdAndMerchantId(Long userId, Long merchantId);
}
