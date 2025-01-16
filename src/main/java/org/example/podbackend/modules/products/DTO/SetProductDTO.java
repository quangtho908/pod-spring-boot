package org.example.podbackend.modules.products.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SetProductDTO {
  @NotNull(message = "Name not null")
  private String name;
  @NotNull(message = "Price not null")
  private Long price;
  private MultipartFile image = null;
  @NotNull(message = "merchant id not null")
  private Long merchantId;
}
