package org.example.podbackend.modules.orders.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.example.podbackend.common.enums.StatusOrder;
import org.example.podbackend.entities.Tables;
import org.example.podbackend.modules.tables.response.TablesFilterResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderFilterResponse {
  private long id;
  private String note;

  @Enumerated(EnumType.STRING)
  private StatusOrder status;

  private long totalPrice;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime createdAt;

  private List<ProductOrderResponseDTO> products = List.of();

  private TablesFilterResponse table;
}
