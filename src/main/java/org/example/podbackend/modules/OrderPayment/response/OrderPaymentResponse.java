package org.example.podbackend.modules.OrderPayment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.example.podbackend.modules.orders.response.OrderResponse;

import java.time.LocalDateTime;

@Data
public class OrderPaymentResponse {
  private OrderResponse order;
  private String image;
  private long price;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime createdAt;
}
