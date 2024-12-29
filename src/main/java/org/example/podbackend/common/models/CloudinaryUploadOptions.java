package org.example.podbackend.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CloudinaryUploadOptions {
  @JsonProperty("use_filename")
  private boolean useFilename = true;

  @JsonProperty("unique_filename")
  private boolean uniqueFilename = true;

  @JsonProperty("asset_folder")
  private String assetFolder;
}
