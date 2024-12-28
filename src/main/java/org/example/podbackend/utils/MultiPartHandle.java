package org.example.podbackend.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

@Component
public class MultiPartHandle {

  @Value("${application.pod.upload.image}")
  private String folder;

  private File uploadFolder;

  @PostConstruct
  private void init () {
    assert folder != null;
    uploadFolder = new File(folder);
    createFolder(uploadFolder);
  }

  public String handle(MultipartFile file, String folder) throws IOException {
    File uploadLocalFolder = new File(uploadFolder.getPath());
    if(!folder.isEmpty()){
      uploadLocalFolder = new File(uploadLocalFolder + File.separator + folder);
      createFolder(uploadLocalFolder);
    }
    String fileName = getFileName(Objects.requireNonNull(file.getOriginalFilename()));
    uploadLocalFolder = new File(uploadLocalFolder.getPath() + File.separator + fileName);
    FileOutputStream fos = new FileOutputStream(uploadLocalFolder);
    InputStream is = file.getInputStream();
    int read;
    byte[] bytes = new byte[1024];
    while ((read = is.read(bytes)) != -1) {
      fos.write(bytes, 0, read);
    }
    fos.flush();
    fos.close();
    is.close();
    return STR."image/\{folder}/\{fileName}";
  }

  public String handle(MultipartFile file) throws IOException {
    return handle(file, "");
  }

  public String getFileName(String filename) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    String ext = filename.substring(filename.lastIndexOf("."));
    digest.update(filename.getBytes());
    digest.update(LocalDateTime.now().toString().getBytes());
    return STR."\{Base64.getUrlEncoder().encodeToString(digest.digest())}.\{ext}";
  }

  private void createFolder(File file) {
    if (!file.exists()) {
      file.mkdirs();
    }
  }
}
