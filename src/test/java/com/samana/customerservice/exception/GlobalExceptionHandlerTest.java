package com.samana.customerservice.exception;

import com.samana.customerservice.controller.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleNotFound() {
        // Arrange
        CustomerNotFoundException exception = new CustomerNotFoundException("Customer with id 123 not found");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Customer with id 123 not found", responseEntity.getBody().getMessage());
        assertEquals(1, responseEntity.getBody().getErrors().size());
        assertEquals("Customer not found!!!", responseEntity.getBody().getErrors().get(0).getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), responseEntity.getBody().getErrors().get(0).getCode());
    }

    @Test
    public void testHandleMethodArgumentNotValid() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        FieldError fieldError = new FieldError("customer", "email", "Email is invalid");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getMessage()).thenReturn("Validation error");

        // Act
        ResponseEntity<Object> responseEntity = exceptionHandler.handleMethodArgumentNotValid(
                ex, headers, HttpStatus.BAD_REQUEST, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Validation error", errorResponse.getMessage());
        assertEquals(2, errorResponse.getErrors().size());
        assertEquals("Validation failed!!!", errorResponse.getErrors().get(0).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorResponse.getErrors().get(0).getCode());
        assertEquals("Email is invalid", errorResponse.getErrors().get(1).getMessage());
        assertEquals("email", errorResponse.getErrors().get(1).getCode());
    }

    @Test
    public void testHandleConstraintViolation() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("email");
        when(violation.getMessage()).thenReturn("must be a well-formed email address");
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        // Act
        ResponseEntity<Object> responseEntity = exceptionHandler.handleConstraintViolation(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Validation failed", errorResponse.getMessage());
        assertEquals(2, errorResponse.getErrors().size());
        assertEquals("Constraint violation!!!", errorResponse.getErrors().get(0).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), errorResponse.getErrors().get(0).getCode());
        assertEquals("must be a well-formed email address", errorResponse.getErrors().get(1).getMessage());
        assertEquals("email", errorResponse.getErrors().get(1).getCode());
    }

    @Test
    public void testHandleAllExceptions() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<Object> responseEntity = exceptionHandler.handleAllExceptions(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Unexpected error", errorResponse.getMessage());
        assertEquals(1, errorResponse.getErrors().size());
        assertEquals("Internal Server Error", errorResponse.getErrors().get(0).getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), errorResponse.getErrors().get(0).getCode());
    }
}
