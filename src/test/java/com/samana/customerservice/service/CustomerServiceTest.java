package com.samana.customerservice.service;

import com.samana.customerservice.persistence.repository.CustomerRepository;
import com.samana.customerservice.utils.CustomerTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void init(){
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void testCalculateTierPlatinum() {
        String tier = customerService.calculateTier(BigDecimal.valueOf(12000), ZonedDateTime.now().minusMonths(2));
        assertEquals(CustomerTier.PLATINUM.getValue (), tier);
    }

    @Test
    void testCalculateTierGold() {
        String tier = customerService.calculateTier(BigDecimal.valueOf(5000), ZonedDateTime.now().minusMonths(10));
        assertEquals(CustomerTier.GOLD.getValue (), tier);
    }

    @Test
    void testCalculateTierSilverDueToOldPurchase() {
        String tier = customerService.calculateTier(BigDecimal.valueOf(5000), ZonedDateTime.now().minusYears(2));
        assertEquals(CustomerTier.SILVER.getValue (), tier);
    }

    @Test
    void testCalculateTierSilverDueToLowSpend() {
        String tier = customerService.calculateTier(BigDecimal.valueOf(100), ZonedDateTime.now());
        assertEquals(CustomerTier.SILVER.getValue(), tier);
    }

    @Test
    void testCalculateTierNullSpend() {
        String tier = customerService.calculateTier(null, ZonedDateTime.now());
        assertEquals(CustomerTier.SILVER.getValue(), tier);
    }

}

