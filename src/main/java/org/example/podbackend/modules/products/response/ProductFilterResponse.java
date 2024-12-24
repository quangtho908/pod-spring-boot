package org.example.podbackend.modules.products.response;

import lombok.Data;

@Data
public class ProductFilterResponse {
  private long id;
  private String name;
  private long price;
}
