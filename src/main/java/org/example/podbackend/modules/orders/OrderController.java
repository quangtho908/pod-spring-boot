package org.example.podbackend.modules.orders;

import jakarta.validation.Valid;
import org.example.podbackend.modules.orders.DTO.ChangeStatusOrderDTO;
import org.example.podbackend.modules.orders.DTO.CreateOrderDTO;
import org.example.podbackend.modules.orders.DTO.RemoveProductOrderDTO;
import org.example.podbackend.modules.orders.DTO.UpdateOrderDTO;
import org.example.podbackend.modules.orders.response.OrderCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;
  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping()
  public ResponseEntity<?> filterOrders(@RequestParam Map<String, String> allParam) {
    return this.orderService.filter(allParam);
  }

  @PostMapping()
  public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody @Valid CreateOrderDTO dto) throws ExecutionException, InterruptedException {
    return this.orderService.createOrder(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Boolean> updateOrder(@RequestBody @Valid UpdateOrderDTO dto, @PathVariable Long id) throws ExecutionException, InterruptedException {
    return this.orderService.updateOrder(dto, id);
  }

  @PutMapping("/removeProduct/{id}")
  public ResponseEntity<Boolean> removeProductOrder(@RequestBody @Valid RemoveProductOrderDTO dto, @PathVariable Long id) throws ExecutionException, InterruptedException {
    return this.orderService.removeProductOrder(dto, id);
  }

  @PostMapping("/changeStatus/{id}")
  public ResponseEntity<Boolean> changeStatus(@PathVariable Long id, @RequestBody @Valid ChangeStatusOrderDTO dto) {
    return this.orderService.changeStatusOrder(id, dto);
  }
}
