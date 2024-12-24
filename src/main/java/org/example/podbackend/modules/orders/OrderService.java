package org.example.podbackend.modules.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.enums.StatusOrder;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.mapper.OrderMapper;
import org.example.podbackend.entities.*;
import org.example.podbackend.modules.orders.DTO.*;
import org.example.podbackend.modules.orders.response.OrderCreateResponse;
import org.example.podbackend.modules.orders.response.OrderFilterResponse;
import org.example.podbackend.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
public class OrderService {
  private final InProgressOrderRepository inProgressOrderRepository;
  private final ProductOrderRepository productOrderRepository;
  private final ProductRepository productRepository;
  private final MerchantRepository merchantRepository;
  private final UserRepository userRepository;
  private final TableRepository tableRepository;
  private final ModelMapper modelMapper;
  private final Executor asyncExecutor;
  private final ObjectMapper objectMapper;
  private final OrderMapper orderMapper;

  public OrderService(
          InProgressOrderRepository inProgressOrderRepository,
          ProductOrderRepository productOrderRepository,
          ProductRepository productRepository,
          MerchantRepository merchantRepository,
          UserRepository userRepository,
          TableRepository tableRepository,
          ModelMapper modelMapper,
          Executor asyncExecutor,
          ObjectMapper objectMapper,
          OrderMapper orderMapper
  ) {
    this.inProgressOrderRepository = inProgressOrderRepository;
    this.productOrderRepository = productOrderRepository;
    this.productRepository = productRepository;
    this.merchantRepository = merchantRepository;
    this.userRepository = userRepository;
    this.tableRepository = tableRepository;
    this.modelMapper = modelMapper;
    this.asyncExecutor = asyncExecutor;
    this.objectMapper = objectMapper;
    this.orderMapper = orderMapper;
  }

  public ResponseEntity<?> filter(@RequestParam Map<String, String> allParam) {
    FilterOrderDTO dto = objectMapper.convertValue(allParam, FilterOrderDTO.class);
    if(dto.getId() != null) {
      InProgressOrder inProgressOrder = this.inProgressOrderRepository.findByIdAndMerchantId(dto.getId(), dto.getMerchantId());
      if(inProgressOrder == null) throw new BadRequestException("Order does not exist");
      return ResponseEntity.ok(orderMapper.mapToResponse(inProgressOrder));
    }
    List<InProgressOrder> inProgressOrders = this.inProgressOrderRepository.filter(
            dto.getMerchantId(),
            dto.getStatuses(),
            dto.getFromDate(),
            dto.getToDate(),
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
    );
    List<OrderFilterResponse> response = orderMapper.mapToResponse(inProgressOrders);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<OrderCreateResponse> createOrder(CreateOrderDTO dto) throws ExecutionException, InterruptedException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetails = (PodUserDetail) authentication.getPrincipal();
    CompletableFuture<Merchant> merchantRead = CompletableFuture.supplyAsync(() ->
            merchantRepository.findByIdAndUserId(dto.getMerchantId(), userDetails.getId()), asyncExecutor
    );
    CompletableFuture<Users> userRead = CompletableFuture.supplyAsync(() ->
      userRepository.findById(userDetails.getId()).orElse(null),
      asyncExecutor
    );
    CompletableFuture<Tables> tableRead = CompletableFuture.supplyAsync(() ->
      tableRepository.findByIdAndMerchantIdAndIsUsedIsFalseAndIsDeletedIsFalse(dto.getTableId(), dto.getMerchantId()), asyncExecutor
    );
    CompletableFuture<Void> allTasks = CompletableFuture.allOf(merchantRead, userRead, tableRead);
    allTasks.join();

    Merchant merchant = merchantRead.get();
    if(merchant == null) throw new BadRequestException("Merchant not found");
    Users user = userRead.get();
    if(user == null) throw new BadRequestException("User not found");
    Tables table = tableRead.get();
    if(table == null) throw new BadRequestException("Table not found");

    InProgressOrder inProgressOrder = modelMapper.map(dto, InProgressOrder.class);
    inProgressOrder.setMerchant(merchant);
    inProgressOrder.setTables(table);
    inProgressOrder.setCreatedBy(user);

    InProgressOrder savedInProgressOrder = inProgressOrderRepository.save(inProgressOrder);

    CompletableFuture<Void> threads = CompletableFuture.allOf(dto.getProducts().stream().map((productOrderDTO) ->
      CompletableFuture.runAsync(() -> {
        Product product = this.productRepository.findByIdAndMerchantId(productOrderDTO.getProductId(), dto.getMerchantId());
        if (product == null) return;
        ProductOrder productOrder = modelMapper.map(productOrderDTO, ProductOrder.class);
        productOrder.setInProgressOrder(savedInProgressOrder);
        productOrder.setProduct(product);
        this.productOrderRepository.save(productOrder);
      }, asyncExecutor)
    ).toArray(CompletableFuture[]::new));
    threads.join();
    table.setUsed(true);
    this.tableRepository.save(table);
    OrderCreateResponse response = modelMapper.map(savedInProgressOrder, OrderCreateResponse.class);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<Boolean> updateOrder(UpdateOrderDTO dto, Long orderId) throws ExecutionException, InterruptedException {
    InProgressOrder inProgressOrder = this.inProgressOrderRepository.findByIdAndMerchantIdAndStatusIn(
            orderId, dto.getMerchantId(), List.of(StatusOrder.WAITING, StatusOrder.PROGRESS)
    );
    if (inProgressOrder == null) throw new BadRequestException("Order does not exist");
    modelMapper.map(dto, inProgressOrder);
    this.inProgressOrderRepository.save(inProgressOrder);

    CompletableFuture<Void> threads = CompletableFuture.allOf(dto.getProducts().stream().map((productOrderDTO) ->
      CompletableFuture.runAsync(() -> {
        ProductOrder productOrder = this.productOrderRepository.findByInProgressOrderIdAndProductId(inProgressOrder.getId(), productOrderDTO.getProductId());
        if (productOrder != null) {
          modelMapper.map(productOrderDTO, productOrder);
          this.productOrderRepository.save(productOrder);
          return;
        };
        Product product = this.productRepository.findByIdAndMerchantId(productOrderDTO.getProductId(), dto.getMerchantId());
        if (product == null) return;
        productOrder = new ProductOrder();
        productOrder.setInProgressOrder(inProgressOrder);
        productOrder.setProduct(product);
        modelMapper.map(productOrderDTO, productOrder);
        this.productOrderRepository.save(productOrder);
      }, asyncExecutor)
    ).toArray(CompletableFuture[]::new));

    threads.join();
    return ResponseEntity.ok(true);
  }

  public ResponseEntity<Boolean> removeProductOrder(RemoveProductOrderDTO dto, Long id) throws ExecutionException, InterruptedException {
    CompletableFuture<Void> threads = CompletableFuture.allOf( Arrays.stream(dto.getProducts()).map(productOrderDTO ->
        CompletableFuture.runAsync(() -> {
          this.productOrderRepository.remove(productOrderDTO.getProductId(), id);
        }, asyncExecutor)
      ).toArray(CompletableFuture[]::new)
    );
    threads.join();
    return ResponseEntity.ok(true);
  }

  public ResponseEntity<Boolean> changeStatusOrder(Long orderId, ChangeStatusOrderDTO dto) {
    InProgressOrder inProgressOrder = this.inProgressOrderRepository.findByIdAndMerchantId(orderId, dto.getMerchantId());
    if (inProgressOrder == null) throw new BadRequestException("Order does not exist");
    inProgressOrder.setStatus(dto.getStatus());
    this.inProgressOrderRepository.save(inProgressOrder);
    if(dto.getStatus().equals(StatusOrder.DONE) || dto.getStatus().equals(StatusOrder.CANCELED)) {
      _resetTable(inProgressOrder.getTables());
    }
    return ResponseEntity.ok(true);
  }

  private void _resetTable(Tables table) {
    table.setUsed(false);
    this.tableRepository.save(table);
  }
}
