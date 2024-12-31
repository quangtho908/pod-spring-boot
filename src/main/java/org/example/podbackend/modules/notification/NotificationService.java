package org.example.podbackend.modules.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.common.models.PushNotification;
import org.example.podbackend.entities.UserLogin;
import org.example.podbackend.repositories.UserLoginRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class NotificationService {
  private final ObjectMapper objectMapper;
  private String expoHost = "https://exp.host/--/api/v2/push/send";
  private final UserLoginRepository userLoginRepository;
  public NotificationService(UserLoginRepository userLoginRepository, ObjectMapper objectMapper) {
    this.userLoginRepository = userLoginRepository;
    this.objectMapper = objectMapper;
  }

  public void notiMerchant (Long merchantId, PushNotification pushNotification) {
    List<UserLogin> userLogins = userLoginRepository.findByMerchantIdAndIsActiveIsTrue(merchantId);
    if (userLogins.isEmpty()) {
      return;
    }
    List<String> tokens = userLogins.stream().map(UserLogin::getFcm).toList();
    pushNotification.setTo(tokens);
    try {
      HttpClient.newHttpClient().send(
              getRequest(pushNotification),
              HttpResponse.BodyHandlers.ofString()
      );
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpRequest getRequest(PushNotification pushNotification) {
    try {
      String json = objectMapper.writeValueAsString(pushNotification);
      return HttpRequest.newBuilder()
              .uri(URI.create(expoHost))
              .header("Content-Type", "application/json")
              .method("POST", HttpRequest.BodyPublishers.ofString(json))
              .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
