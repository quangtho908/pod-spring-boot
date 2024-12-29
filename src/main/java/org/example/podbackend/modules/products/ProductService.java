package org.example.podbackend.modules.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.Product;
import org.example.podbackend.modules.products.DTO.SetProductDTO;
import org.example.podbackend.modules.products.DTO.FilterProductDTO;
import org.example.podbackend.modules.products.response.ProductFilterResponse;
import org.example.podbackend.repositories.MerchantRepository;
import org.example.podbackend.repositories.ProductRepository;
import org.example.podbackend.utils.CloudinaryService;
import org.example.podbackend.utils.MultiPartHandle;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final MultiPartHandle multiPartHandle;
  private final MerchantRepository merchantRepository;
  private final CloudinaryService cloudinaryService;

  public ProductService(
          ProductRepository productRepository,
          ModelMapper modelMapper,
          ObjectMapper objectMapper,
          MultiPartHandle multiPartHandle,
          MerchantRepository merchantRepository,
          CloudinaryService cloudinaryService) {
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
    this.objectMapper = objectMapper;
    this.multiPartHandle = multiPartHandle;
    this.merchantRepository = merchantRepository;
    this.cloudinaryService = cloudinaryService;
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

  public ResponseEntity<Boolean> addProduct(SetProductDTO createProductDTO) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) authentication.getPrincipal();
    Merchant merchant = this.merchantRepository.findByIdAndUserId(createProductDTO.getMerchantId(), userDetail.getId());
    if(merchant == null) throw new NotFoundException("Merchant not found");
    Product product = new Product();
    uploadImage(createProductDTO, product);
    product.setMerchant(merchant);
    this.productRepository.save(product);
    return ResponseEntity.ok(true);
  }

  public ResponseEntity<Boolean> updateProduct(Long id, SetProductDTO dto) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) authentication.getPrincipal();
    Merchant merchant = this.merchantRepository.findByIdAndUserId(dto.getMerchantId(), userDetail.getId());
    if(merchant == null) throw new BadRequestException("Merchant not found");
    Product product = this.productRepository.findByIdAndMerchantId(id, merchant.getId());
    if(product == null) throw new NotFoundException("Product not found");
    uploadImage(dto, product);
    this.productRepository.save(product);
    return ResponseEntity.ok(true);
  }

  private void uploadImage(SetProductDTO dto, Product product) throws IOException {
    if(dto.getImage() != null && !dto.getImage().isEmpty()) {
      String localUrl = multiPartHandle.handle(dto.getImage());
      String url = cloudinaryService.upload(localUrl, "products");
      dto.setImage(null);
      if(product.getImage() != null && !product.getImage().isEmpty()) {
        cloudinaryService.delete(product.getImage());
      }
      product.setImage(url);
      multiPartHandle.delete(localUrl);
    }
    this.modelMapper.map(dto, product);
  }
}
