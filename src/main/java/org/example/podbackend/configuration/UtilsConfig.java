package org.example.podbackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class UtilsConfig {

  @Bean
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setSkipNullEnabled(true);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    modelMapper.getConfiguration().setPropertyCondition((MappingContext<Object, Object> context) ->
            {
              if (context.getSource() instanceof String) {
                return !((String) context.getSource()).isEmpty();
              }
              return true;
            }
    );
    return modelMapper;
  }

  @Bean
  public Executor asyncExecutor() {
    return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
  }

  @Bean
  public MailjetClient mailjetClient() {

    ClientOptions options = ClientOptions.builder()
            .apiKey("0591f9ae8b4baee079a7e6611a0a33dd")
            .apiSecretKey("e51551689be44bb4f85f2c34a5a33d96")
            .build();

    return new MailjetClient(options);
  }
}
