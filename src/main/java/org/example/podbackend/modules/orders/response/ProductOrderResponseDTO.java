package org.example.podbackend.modules.orders.response;

import lombok.Data;

@Data
public class ProductOrderResponseDTO {

  private long id;
  private String name;
  private int quantity;
  private long price;
}
