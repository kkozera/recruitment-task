package pl.kkozera.recruitment_task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPageRequestException extends RuntimeException {
  public InvalidPageRequestException(String message) {
    super(message);
  }
}