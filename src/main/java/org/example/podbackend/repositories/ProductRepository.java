package org.example.podbackend.repositories;

import org.example.podbackend.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends BaseRepository<Product, Long> {
  Product findByIdAndMerchantId(Long id, Long merchantId);

  @Query("SELECT p FROM Product p WHERE p.merchant.id = ?1 AND p.name LIKE %?2%")
  List<Product> filter(Long merchantId, String name, Pageable pageable);
}
