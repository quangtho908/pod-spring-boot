package org.example.podbackend.modules.orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.podbackend.entities.InProgressOrder;
import org.example.podbackend.entities.Product;
import org.example.podbackend.entities.ProductOrder;
import org.example.podbackend.modules.orders.DTO.ProductOrderDTO;
import org.example.podbackend.repositories.ProductOrderRepository;
import org.example.podbackend.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ProductOrderService {

  @PersistenceContext
  private EntityManager entityManager;

  private final ProductOrderRepository productOrderRepository;
  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;
  public ProductOrderService(
          ProductOrderRepository productOrderRepository,
          ProductRepository productRepository,
          ModelMapper modelMapper) {
    this.productOrderRepository = productOrderRepository;
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
  }

  public void bulkSave(List<ProductOrderDTO> products, InProgressOrder inProgressOrder) {
    products.forEach((productOrderDTO) -> {
        Product product = this.productRepository.findByIdAndMerchantId(productOrderDTO.getProductId(), inProgressOrder.getMerchant().getId());
        if (product == null) return;
        ProductOrder productOrder = modelMapper.map(productOrderDTO, ProductOrder.class);
        productOrder.setInProgressOrder(inProgressOrder);
        productOrder.setProduct(product);
        productOrderRepository.save(productOrder);
      }
    );
  }

  public void bulkUpdate(List<ProductOrderDTO> products, InProgressOrder inProgressOrder) {
    products.forEach((productOrderDTO) -> {
      ProductOrder productOrder = this.productOrderRepository.findByInProgressOrderIdAndProductId(inProgressOrder.getId(), productOrderDTO.getProductId());
      if (productOrder != null) {
        modelMapper.map(productOrderDTO, productOrder);
        this.productOrderRepository.save(productOrder);
        return;
      };
      Product product = this.productRepository.findByIdAndMerchantId(productOrderDTO.getProductId(), inProgressOrder.getMerchant().getId());
      if (product == null) return;
      productOrder = new ProductOrder();
      productOrder.setInProgressOrder(inProgressOrder);
      productOrder.setProduct(product);
      modelMapper.map(productOrderDTO, productOrder);
      this.productOrderRepository.save(productOrder);
    });
  }
}
