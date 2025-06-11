package com.samana.customerservice.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private final String message;
    private List<Error> errors;
}
