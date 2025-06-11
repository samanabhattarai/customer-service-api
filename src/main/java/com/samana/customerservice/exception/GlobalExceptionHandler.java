package com.samana.customerservice.exception;

import com.samana.customerservice.controller.response.Error;
import com.samana.customerservice.controller.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFound(CustomerNotFoundException ex) {
        List<com.samana.customerservice.controller.response.Error> errors = new ArrayList<> ();
        errors.add(com.samana.customerservice.controller.response.Error.builder().message("Customer not found!!!").code (HttpStatus.NOT_FOUND.toString()).build());
        return new ResponseEntity<>(ErrorResponse.builder().message(ex.getMessage()).errors (errors).build(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<com.samana.customerservice.controller.response.Error> errors = new ArrayList<> ();
        errors.add(com.samana.customerservice.controller.response.Error.builder().message("Validation failed!!!").code (HttpStatus.BAD_REQUEST.toString()).build());
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(com.samana.customerservice.controller.response.Error.builder().message(error.getDefaultMessage()).code (error.getField ()).build());
        }
        ErrorResponse apiError = ErrorResponse.builder().message(ex.getMessage()).errors (errors).build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<com.samana.customerservice.controller.response.Error> errors = new ArrayList<> ();
        errors.add(com.samana.customerservice.controller.response.Error.builder().message("Constraint violation!!!").code (HttpStatus.BAD_REQUEST.toString()).build());
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.add(com.samana.customerservice.controller.response.Error.builder().message(message).code (fieldName).build());
        });
        ErrorResponse apiError = ErrorResponse.builder().message(ex.getMessage()).errors (errors).build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }




    // Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        List<com.samana.customerservice.controller.response.Error> errors = new ArrayList<> ();
        errors.add(Error.builder().message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).code (HttpStatus.INTERNAL_SERVER_ERROR.toString()).build());
        ErrorResponse apiError = ErrorResponse.builder().message(ex.getMessage()).errors (errors).build();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
