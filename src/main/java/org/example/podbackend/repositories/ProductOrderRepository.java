package org.example.podbackend.repositories;

import org.example.podbackend.entities.ProductOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductOrderRepository extends BaseRepository<ProductOrder, Long> {
  public ProductOrder findByInProgressOrderIdAndProductId(Long userId, Long merchantId);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM ProductOrder po WHERE po.product.id = ?1 AND po.inProgressOrder.id = ?2")
  void remove(long productId, long inProgressOrderId);
}
