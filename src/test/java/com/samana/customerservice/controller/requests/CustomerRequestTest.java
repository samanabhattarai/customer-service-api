package com.samana.customerservice.controller.requests;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidCustomerRequest() {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmail() {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Doe");
        request.setEmail("invalid-email");

        var violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    public void testEmptyName() {
        CustomerRequest request = new CustomerRequest();
        request.setName("");
        request.setEmail("john.doe@example.com");

        var violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    public void testNullName() {
        CustomerRequest request = new CustomerRequest();
        request.setName(null);
        request.setEmail("john.doe@example.com");

        var violations = validator.validate(request);
        assertEquals(1, violations.size());
    }
}
