package org.example.podbackend.modules.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.enums.StatusOrder;
import org.example.podbackend.common.enums.TypeNoti;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.mapper.OrderMapper;
import org.example.podbackend.common.models.DataNotification;
import org.example.podbackend.common.models.PushNotification;
import org.example.podbackend.entities.*;
import org.example.podbackend.modules.notification.NotificationService;
import org.example.podbackend.modules.orders.DTO.*;
import org.example.podbackend.modules.orders.response.OrderResponse;
import org.example.podbackend.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final NotificationService notificationService;
    private final ProductOrderService productOrderService;
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
            OrderMapper orderMapper,
            NotificationService notificationService,
            ProductOrderService productOrderService
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
      this.notificationService = notificationService;
      this.productOrderService = productOrderService;
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
      List<OrderResponse> response = orderMapper.mapToResponse(inProgressOrders);
      return ResponseEntity.ok(response);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<OrderResponse> createOrder(CreateOrderDTO dto) throws ExecutionException, InterruptedException {
      InProgressOrder savedInProgressOrder = this.create(dto);
      productOrderService.bulkSave(dto.getProducts(), savedInProgressOrder);
      Tables table = savedInProgressOrder.getTables();
      Merchant merchant = savedInProgressOrder.getMerchant();
      if(table != null ) {
        table.setUsed(true);
        this.tableRepository.save(table);
      }

      PushNotification pushNotification = new PushNotification();
      pushNotification.setTitle(merchant.getName());
      pushNotification.setBody(STR."Đơn hàng mới \{savedInProgressOrder.getId()} được tạo");
      pushNotification.setData(new DataNotification(TypeNoti.CREATE_ORDER));
      notificationService.notiMerchant(merchant.getId(), pushNotification);

      OrderResponse filterResponse = orderMapper.mapToResponse(savedInProgressOrder);
      return ResponseEntity.ok(filterResponse);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<Boolean> updateOrder(UpdateOrderDTO dto, Long orderId) {
      InProgressOrder inProgressOrder = this.inProgressOrderRepository.findByIdAndMerchantIdAndStatusIn(
              orderId, dto.getMerchantId(), List.of(StatusOrder.WAITING, StatusOrder.PROGRESS)
      );
      if (inProgressOrder == null) throw new BadRequestException("Order does not exist");
      modelMapper.map(dto, inProgressOrder);
      this.inProgressOrderRepository.save(inProgressOrder);

      this.productOrderService.bulkUpdate(dto.getProducts(), inProgressOrder);
      return ResponseEntity.ok(true);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<Boolean> removeProductOrder(RemoveProductOrderDTO dto, Long id) {
      Arrays.stream(dto.getProducts()).forEach(product -> {
        this.productOrderRepository.remove(product.getProductId(), id);
      });
      return ResponseEntity.ok(true);
    }

    public ResponseEntity<Boolean> changeStatusOrder(Long orderId, ChangeStatusOrderDTO dto) {
      InProgressOrder inProgressOrder = this.inProgressOrderRepository.findByIdAndMerchantId(orderId, dto.getMerchantId());
      if (inProgressOrder == null) throw new BadRequestException("Order does not exist");
      inProgressOrder.setStatus(dto.getStatus());
      this.inProgressOrderRepository.save(inProgressOrder);
      if((dto.getStatus().equals(StatusOrder.DONE) || dto.getStatus().equals(StatusOrder.CANCELED)) && inProgressOrder.getTables() != null) {
        _resetTable(inProgressOrder.getTables());
      }
      return ResponseEntity.ok(true);
    }

    public InProgressOrder create(CreateOrderDTO dto) throws ExecutionException, InterruptedException {
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
              dto.getTableId() == null ? null : tableRepository.findByIdAndMerchantIdAndIsUsedIsFalse(dto.getTableId(), dto.getMerchantId()), asyncExecutor
      );
      CompletableFuture<Void> allTasks = CompletableFuture.allOf(merchantRead, userRead, tableRead);
      allTasks.join();

      Merchant merchant = merchantRead.get();
      if(merchant == null) throw new BadRequestException("Merchant not found");
      Users user = userRead.get();
      if(user == null) throw new BadRequestException("User not found");
      Tables table = tableRead.get();

      InProgressOrder inProgressOrder = modelMapper.map(dto, InProgressOrder.class);
      inProgressOrder.setMerchant(merchant);
      if(table != null)  inProgressOrder.setTables(table);;
      inProgressOrder.setCreatedBy(user);

      return inProgressOrderRepository.save(inProgressOrder);
    }

    private void _resetTable(Tables table) {
      table.setUsed(false);
      this.tableRepository.save(table);
    }
}
