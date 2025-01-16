package org.example.podbackend.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UtilsService {
  public String generateRandomString() {
    Random random = new Random();
    int number = 100000 + random.nextInt(900000);
    return String.valueOf(number);
  }
}
