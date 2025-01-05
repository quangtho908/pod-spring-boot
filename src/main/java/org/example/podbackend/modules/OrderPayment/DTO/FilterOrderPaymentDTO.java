package org.example.podbackend.modules.OrderPayment.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.podbackend.common.enums.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FilterOrderPaymentDTO {
  private Long id;

  @NotNull
  private Long merchantId;

  @NotNull
  private int page;

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod = PaymentMethod.CASH;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime fromDate = LocalDate.now().atStartOfDay();

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime toDate = LocalDate.now().atStartOfDay().plusDays(1);
}
