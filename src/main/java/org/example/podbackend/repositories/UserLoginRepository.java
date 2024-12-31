package org.example.podbackend.repositories;

import org.example.podbackend.entities.UserLogin;

import java.util.List;

public interface UserLoginRepository extends BaseRepository<UserLogin, Long> {
  UserLogin findByUserIdAndMerchantId(long user_id, long merchant_id);

  List<UserLogin> findByUserIdAndIsActiveIsTrue(long user_id);

  List<UserLogin> findByMerchantIdAndIsActiveIsTrue(long merchant_id);
}
