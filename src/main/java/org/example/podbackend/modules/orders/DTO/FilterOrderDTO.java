package org.example.podbackend.modules.orders.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.podbackend.common.enums.StatusOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FilterOrderDTO {
  private Long id;

  @NotNull(message = "Merchant id is required")
  private Long merchantId;

  private List<StatusOrder> statuses = List.of(StatusOrder.WAITING, StatusOrder.PROGRESS);

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime fromDate = LocalDate.now().atStartOfDay();

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime toDate = LocalDate.now().atStartOfDay().plusDays(1);
}
