package org.example.podbackend.modules.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.entities.Product;
import org.example.podbackend.modules.products.DTO.FilterProductDTO;
import org.example.podbackend.modules.products.response.ProductFilterResponse;
import org.example.podbackend.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  public ProductService(ProductRepository productRepository, ModelMapper modelMapper, ObjectMapper objectMapper) {
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
    this.objectMapper = objectMapper;
  }

  public ResponseEntity<?> filter(Map<String, String> filters) {
    FilterProductDTO filterProductDTO = objectMapper.convertValue(filters, FilterProductDTO.class);
    if(filterProductDTO.getId() != null) {
      Product product = this.productRepository.findByIdAndMerchantId(filterProductDTO.getId(), filterProductDTO.getMerchantId());
      if (product == null) throw new NotFoundException("Product not found");
      return ResponseEntity.ok(this.modelMapper.map(product, FilterProductDTO.class));
    }
    List<Product> products = this.productRepository.filter(filterProductDTO.getMerchantId(), filterProductDTO.getName(), Pageable.unpaged());
    List<ProductFilterResponse> responses = products.stream().map(product -> this.modelMapper.map(product, ProductFilterResponse.class)).collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }
}
