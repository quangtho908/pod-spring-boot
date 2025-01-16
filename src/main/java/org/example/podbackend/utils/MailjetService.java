package org.example.podbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MailjetService {
  private final String mailSender = "quangtho23062002@gmail.com";
  private final MailjetClient mailjetClient;

  public MailjetService(MailjetClient mailjetClient) {
    this.mailjetClient = mailjetClient;
  }

  public void sendMail(String to, String subject, String text) {
    try {
      MailjetRequest request = new MailjetRequest(Emailv31.resource);
      request.property(Emailv31.MESSAGES, new JSONArray()
        .put(new JSONObject()
          .put(Emailv31.Message.FROM, new JSONObject()
            .put("Email", mailSender)
            .put("Name", "<No Reply>")
          )
          .put(Emailv31.Message.TO, new JSONArray()
            .put(new JSONObject().put("Email", to). put("Name", to))
          )
          .put(Emailv31.Message.SUBJECT, subject)
          .put(Emailv31.Message.TEXTPART, text)
        )
      );
      MailjetResponse response = mailjetClient.post(request);
    } catch ( MailjetException e ) {
      throw new RuntimeException(e);
    }

  }
}
