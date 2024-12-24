package org.example.podbackend.modules.merchants.DTO;

import lombok.Data;
import org.example.podbackend.common.models.BaseDTO;

@Data
public class UpdateMerchantDTO extends BaseDTO {
  private String address;
  private String phoneNumber;
}
