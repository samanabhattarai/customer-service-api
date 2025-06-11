package com.samana.customerservice.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private String id;
    private String name;
    private String email;
    private String tier;
    private Double annualSpend;
    private String lastPurchaseDate;
}
