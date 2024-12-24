package org.example.podbackend.modules.merchants.response;

import lombok.Data;

@Data
public class MerchantCreateResponse {
  private long merchantId;
  private String merchantName;
}
