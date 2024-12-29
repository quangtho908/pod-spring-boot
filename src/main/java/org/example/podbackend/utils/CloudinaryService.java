package org.example.podbackend.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import org.cloudinary.json.JSONObject;
import org.example.podbackend.common.models.CloudinaryUploadOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
public class CloudinaryService {
  @Value("${application.pod.cloudinary}")
  private String cloudConnectString;
  private Cloudinary cloudinary;
  private final ObjectMapper objectMapper;
  public CloudinaryService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void init() {
    cloudinary = new Cloudinary(cloudConnectString);
  }

  public String upload(String file, CloudinaryUploadOptions options) {
    try {
      String json = objectMapper.writeValueAsString(options);
      JSONObject jsonObject = new JSONObject(json);
      Map uploaded = cloudinary.uploader().upload(file, ObjectUtils.toMap(jsonObject));
      return uploaded.get("url").toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String upload(String file) {
    CloudinaryUploadOptions options = new CloudinaryUploadOptions();
    return upload(file, options);
  }

  public String upload(String file, String folder) {
    CloudinaryUploadOptions options = new CloudinaryUploadOptions();
    options.setAssetFolder(folder);
    return upload(file, options);
  }

  public void delete(String uri) {
    try {
      String publicId = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
      cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
              "resource_type", "image",
              "type", "upload"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
