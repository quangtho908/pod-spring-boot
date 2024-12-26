package org.example.podbackend.modules.OrderPayment;

import org.example.podbackend.entities.OrderPayment;
import org.example.podbackend.modules.OrderPayment.DTO.PaymentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orderPayments")
public class OrderPaymentController {
  private final OrderPaymentService orderPaymentService;
  public OrderPaymentController(OrderPaymentService orderPaymentService) {
    this.orderPaymentService = orderPaymentService;
  }

  @PostMapping
  public ResponseEntity<Boolean> createOrderPayment(@RequestBody  PaymentDTO dto) throws ExecutionException, InterruptedException {
    return this.orderPaymentService.create(dto);
  }
}
