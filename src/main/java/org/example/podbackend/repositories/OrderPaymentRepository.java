package org.example.podbackend.repositories;

import org.example.podbackend.common.enums.PaymentMethod;
import org.example.podbackend.entities.OrderPayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderPaymentRepository extends BaseRepository<OrderPayment, Long> {
  OrderPayment findByInProgressOrderId(Long id);
  @Query("SELECT op " +
          "FROM OrderPayment op " +
          "WHERE op.merchant.id = :merchantId " +
          "AND op.paymentMethod = :paymentMethod " +
          "AND op.createdAt >= :fromDate AND op.createdAt <= :toDate")
  List<OrderPayment> filter(Long merchantId, PaymentMethod paymentMethod, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
