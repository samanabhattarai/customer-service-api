package com.samana.customerservice.persistence.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    private String email;

    @Column(name = "annual_spend")
    private BigDecimal annualSpend;

    @Column(name = "last_purchase_date")
    private ZonedDateTime lastPurchaseDate;

}
