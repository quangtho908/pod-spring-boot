package org.example.podbackend.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnAuthorizedException extends BaseException {
  public UnAuthorizedException(String message) {
    super(message);
  }
}
