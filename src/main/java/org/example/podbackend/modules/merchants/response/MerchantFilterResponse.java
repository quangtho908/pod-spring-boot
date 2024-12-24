package org.example.podbackend.modules.merchants.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class MerchantFilterResponse {

  private long id;
  private String name;
  private String address;
  private String phoneNumber;
  private String role;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime createdAt;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM/dd/yyyy, HH:mm:ss a")
  private LocalDateTime updatedAt;
}
