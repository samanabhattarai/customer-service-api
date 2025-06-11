package com.samana.customerservice.controller;

import com.samana.customerservice.controller.requests.CustomerRequest;
import com.samana.customerservice.controller.response.CustomerResponse;
import com.samana.customerservice.exception.CustomerNotFoundException;
import com.samana.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController (CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> getCustomers (
            @RequestParam(name = "name", required = false)  String name,
            @RequestParam(name = "email", required = false) String email) {

        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(customerService.getCustomersByName(name));
        } else if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(customerService.getCustomerByEmail(email));
        }

        return ResponseEntity.ok(customerService.getCustomers());

    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> saveCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        CustomerResponse savedCustomer = customerService.saveCustomer(customerRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCustomer.getId())
                .toUri();
         return ResponseEntity.created(location).body(savedCustomer);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable String id, @Valid @RequestBody CustomerRequest customerRequest) throws CustomerNotFoundException {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerRequest));
    }


}