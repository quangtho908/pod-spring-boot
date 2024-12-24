package org.example.podbackend.Security;

import org.example.podbackend.common.models.ExceptionResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionResponseModel> handleException(RuntimeException ex) {
    ExceptionResponseModel responseModel = new ExceptionResponseModel();
    HttpStatus status = getHttpStatusFromException(ex);
    responseModel.setMessage(ex.getMessage());
    return new ResponseEntity<>(responseModel, status);
  }

  private HttpStatus getHttpStatusFromException(Exception ex) {
    ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);

    if (responseStatus != null) {
      return responseStatus.value();
    }

    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );
    return ResponseEntity.badRequest().body(errors);
  }
}
