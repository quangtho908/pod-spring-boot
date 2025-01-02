package org.example.podbackend.modules.OrderPayment;

import jakarta.validation.Valid;
import org.example.podbackend.entities.OrderPayment;
import org.example.podbackend.modules.OrderPayment.DTO.FilterOrderPaymentDTO;
import org.example.podbackend.modules.OrderPayment.DTO.PaymentDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orderPayments")
public class OrderPaymentController {
  private final OrderPaymentService orderPaymentService;
  public OrderPaymentController(OrderPaymentService orderPaymentService) {
    this.orderPaymentService = orderPaymentService;
  }

//  @GetMapping()
//  public ResponseEntity<?> filter(@ModelAttribute FilterOrderPaymentDTO dto) {
//
//  }

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Boolean> createOrderPayment(@ModelAttribute @Valid PaymentDTO dto) throws ExecutionException, InterruptedException, IOException {
    return this.orderPaymentService.create(dto);
  }
}
