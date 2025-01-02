package org.example.podbackend.repositories;

import org.example.podbackend.entities.OrderPayment;

import java.util.Optional;

public interface OrderPaymentRepository extends BaseRepository<OrderPayment, Long> {
  OrderPayment findByInProgressOrderId(Long id);
}
