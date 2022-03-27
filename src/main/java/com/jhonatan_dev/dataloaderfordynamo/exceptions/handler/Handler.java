package com.jhonatan_dev.dataloaderfordynamo.exceptions.handler;

import com.jhonatan_dev.dataloaderfordynamo.exceptions.InternalServerErrorException;
import com.jhonatan_dev.dataloaderfordynamo.exceptions.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class Handler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {InternalServerErrorException.class})
  protected ResponseEntity<Object> handleConflict(
      InternalServerErrorException ex, WebRequest request) {
    return handleExceptionInternal(
        ex, ex.getMensaje(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> handleConflict(NotFoundException ex, WebRequest request) {
    return handleExceptionInternal(
        ex, ex.getMensaje(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }
}
