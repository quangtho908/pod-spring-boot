package org.example.podbackend.repositories;

import org.example.podbackend.entities.Users;

import java.util.Optional;

public interface UserRepository extends BaseRepository<Users, Long> {
  Optional<Users> findByEmailOrPhoneNumber(String email, String phoneNumber);
  Optional<Users> findByPhoneNumber(String phoneNumber);
}
