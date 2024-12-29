package org.example.podbackend.modules.OrderPayment.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.podbackend.common.enums.PaymentMethod;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PaymentDTO {
  @NotNull(message = "order id not null")
  private Long orderId;

  @NotNull(message = "merchant id not null")
  private Long merchantId;

  @NotNull(message = "price not null")
  private Long price;

  private PaymentMethod paymentMethod;

  private MultipartFile image;
}
