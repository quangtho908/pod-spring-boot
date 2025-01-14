package org.example.podbackend.modules.users.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SetAvatarDTO {
  private MultipartFile image;
}
