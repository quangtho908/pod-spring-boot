package org.example.podbackend.modules.merchants.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadAvatarDTO {
  private MultipartFile image = null;
}
