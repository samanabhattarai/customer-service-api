package com.samana.customerservice.service;

import com.samana.customerservice.controller.requests.CustomerRequest;
import com.samana.customerservice.controller.response.CustomerResponse;
import com.samana.customerservice.exception.CustomerNotFoundException;
import com.samana.customerservice.persistence.entity.CustomerEntity;
import com.samana.customerservice.persistence.repository.CustomerRepository;
import com.samana.customerservice.utils.CustomerTier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }



    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToCustomerDetails)
                .toList();
    }

    public List<CustomerResponse> getCustomersByName(String name) {
        log.info("Retrieving customer by name: {}", name);
        return customerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToCustomerDetails)
                .toList();
    }

    public List<CustomerResponse> getCustomerByEmail(String email) {
        log.info("Retrieving customer by email: {}", email);
        return customerRepository.findByEmailIgnoreCase(email).stream()
                .map(this::mapToCustomerDetails)
                .toList();
    }

    private CustomerResponse mapToCustomerDetails (CustomerEntity customerEntity) {
        return CustomerResponse.builder()
                .id(customerEntity.getId().toString())
                .name(customerEntity.getName())
                .email(customerEntity.getEmail())
                .tier(calculateTier(customerEntity.getAnnualSpend(), customerEntity.getLastPurchaseDate()))
                .annualSpend(customerEntity.getAnnualSpend()!=null ? customerEntity.getAnnualSpend().doubleValue(): null)
                .lastPurchaseDate(customerEntity.getLastPurchaseDate() != null ? customerEntity.getLastPurchaseDate().toString(): null)
                .build();
    }


    public String calculateTier(BigDecimal spend, ZonedDateTime lastPurchaseDate) {
        ZonedDateTime now = ZonedDateTime.now();

        if (spend != null &&spend.doubleValue () >= 1000 && spend.doubleValue () < 10000 &&
                lastPurchaseDate != null &&
                lastPurchaseDate.isAfter(now.minusMonths(12))) {
            return CustomerTier.GOLD.getValue();
        }

        if (spend != null &&spend.doubleValue () >= 10000 &&
                lastPurchaseDate != null &&
                lastPurchaseDate.isAfter(now.minusMonths(6))) {
            return CustomerTier.PLATINUM.getValue();
        }

        return CustomerTier.SILVER.getValue();
    }

    public CustomerResponse saveCustomer (CustomerRequest customerRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        saveCustomer(customerRequest, customerEntity);
        return mapToCustomerDetails(customerEntity);
    }

    private void saveCustomer (CustomerRequest customerRequest, CustomerEntity customerEntity) {
        customerEntity.setName(customerRequest.getName());
        customerEntity.setEmail(customerRequest.getEmail());
        customerEntity.setAnnualSpend(customerRequest.getAnnualSpend() != null ? BigDecimal.valueOf (customerRequest.getAnnualSpend()) : null);
        customerEntity.setLastPurchaseDate(customerRequest.getLastPurchaseDate()!=null ? ZonedDateTime.parse(customerRequest.getLastPurchaseDate()): null);
        customerRepository.save(customerEntity);
    }

    public void deleteCustomer (String customerId) throws CustomerNotFoundException {
        if (StringUtils.isNotEmpty(customerId)  &&
                customerRepository.findById(UUID.fromString (customerId)).isPresent()) {
            customerRepository.deleteById(UUID.fromString (customerId));
        } else {
            throw new CustomerNotFoundException ("Customer not found with id: " + customerId);
        }
    }

    public CustomerResponse getCustomer (String customerId) throws CustomerNotFoundException {
        if (StringUtils.isNotEmpty(customerId) &&
                customerRepository.findById(UUID.fromString (customerId)).isPresent()) {
            return mapToCustomerDetails(customerRepository.findById(UUID.fromString (customerId)).get());
        } else {
            throw new CustomerNotFoundException ("Customer not found with id: " + customerId);
        }
    }

    public CustomerResponse updateCustomer (String customerId, @Validated CustomerRequest customerRequest) throws CustomerNotFoundException {
        if (StringUtils.isNotEmpty(customerId) &&
                customerRepository.findById(UUID.fromString (customerId)).isPresent()) {
            CustomerEntity customerEntity = customerRepository.findById(UUID.fromString (customerId)).get();
            saveCustomer (customerRequest, customerEntity);
            return mapToCustomerDetails(customerEntity);
        } else {
            throw new CustomerNotFoundException ("Customer not found with id: " + customerId);
        }
    }
}
