package org.example.podbackend.modules.OrderPayment;

import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.enums.StatusOrder;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.entities.InProgressOrder;
import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.OrderPayment;
import org.example.podbackend.modules.OrderPayment.DTO.PaymentDTO;
import org.example.podbackend.modules.orders.DTO.ChangeStatusOrderDTO;
import org.example.podbackend.modules.orders.OrderService;
import org.example.podbackend.repositories.InProgressOrderRepository;
import org.example.podbackend.repositories.MerchantRepository;
import org.example.podbackend.repositories.OrderPaymentRepository;
import org.example.podbackend.utils.CloudinaryService;
import org.example.podbackend.utils.MultiPartHandle;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
public class OrderPaymentService {
  private final OrderPaymentRepository orderPaymentRepository;
  private final MerchantRepository merchantRepository;
  private final InProgressOrderRepository inProgressOrderRepository;
  private final Executor asyncExecutor;
  private final ModelMapper modelMapper;
  private final OrderService orderService;
  private final MultiPartHandle multiPartHandle;
  private final CloudinaryService cloudinaryService;
  public OrderPaymentService(
          OrderPaymentRepository orderPaymentRepository,
          MerchantRepository merchantRepository,
          InProgressOrderRepository inProgressOrderRepository,
          Executor asyncExecutor, ModelMapper modelMapper,
          OrderService orderService,
          MultiPartHandle multiPartHandle,
          CloudinaryService cloudinaryService
  ) {
    this.orderPaymentRepository = orderPaymentRepository;
    this.merchantRepository = merchantRepository;
    this.inProgressOrderRepository = inProgressOrderRepository;
    this.asyncExecutor = asyncExecutor;
    this.modelMapper = modelMapper;
    this.orderService = orderService;
    this.multiPartHandle = multiPartHandle;
    this.cloudinaryService = cloudinaryService;
  }

  public ResponseEntity<Boolean> create(PaymentDTO dto) throws ExecutionException, InterruptedException, IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) auth.getPrincipal();
    CompletableFuture<Merchant> merchantRead = CompletableFuture.supplyAsync(
            () -> this.merchantRepository.findByIdAndUserId(dto.getMerchantId(), userDetail.getId()),
            asyncExecutor
    );
    CompletableFuture<InProgressOrder> orderRead = CompletableFuture.supplyAsync(
            () -> this.inProgressOrderRepository.findByIdAndMerchantIdAndStatusIn(dto.getOrderId(), dto.getMerchantId(), List.of(StatusOrder.WAITING, StatusOrder.PROGRESS)),
            asyncExecutor
    );
    CompletableFuture<Void> allRead = CompletableFuture.allOf(merchantRead, orderRead);
    allRead.join();
    Merchant merchant = merchantRead.get();
    if (merchant == null) {
      throw new BadRequestException("Merchant not found");
    }
    InProgressOrder order = orderRead.get();
    if (order == null) {
      throw new NotFoundException("Order not found");
    }
    OrderPayment orderPayment = new OrderPayment();
    if(dto.getImage() != null) {
      String localPath = multiPartHandle.handle(dto.getImage());
      String url = cloudinaryService.upload(localPath, "orders_payments");
      dto.setImage(null);
      orderPayment.setImage(url);
      multiPartHandle.delete(localPath);
    }

    modelMapper.map(dto, orderPayment);
    orderPayment.setMerchant(merchant);
    orderPayment.setInProgressOrder(order);
    this.orderPaymentRepository.save(orderPayment);
    ChangeStatusOrderDTO changeStatusOrderDTO = new ChangeStatusOrderDTO();
    changeStatusOrderDTO.setStatus(StatusOrder.DONE);
    changeStatusOrderDTO.setMerchantId(merchant.getId());
    this.orderService.changeStatusOrder(dto.getOrderId(), changeStatusOrderDTO);
    return ResponseEntity.ok(true);
  }
}
