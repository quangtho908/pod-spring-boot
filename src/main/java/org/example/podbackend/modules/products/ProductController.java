package org.example.podbackend.modules.products;

import jakarta.validation.Valid;
import org.example.podbackend.modules.products.DTO.SetProductDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<?> filterProduct(@RequestParam Map<String, String> requestParams) {
    return this.productService.filter(requestParams);
  }

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Boolean> addProduct(@ModelAttribute @Valid SetProductDTO dto) throws IOException {
    return this.productService.addProduct(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Boolean> updateProduct(@PathVariable Long id, @ModelAttribute @Valid SetProductDTO dto) throws IOException {
    return this.productService.updateProduct(id, dto);
  }
}
