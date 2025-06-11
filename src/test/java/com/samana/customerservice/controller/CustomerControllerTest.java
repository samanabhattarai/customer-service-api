package com.samana.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samana.customerservice.controller.requests.CustomerRequest;
import com.samana.customerservice.controller.response.CustomerResponse;
import com.samana.customerservice.exception.CustomerNotFoundException;
import com.samana.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;
    private final CustomerResponse customerResponse = CustomerResponse.builder()
            .id ("1")
            .name("John Doe")
            .email("john.doe@example.com").build ();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllCustomers() throws Exception {
        List<CustomerResponse> customers = Collections.singletonList(customerResponse);
        when(customerService.getCustomers()).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(customerService).getCustomers();
    }

    @Test
    public void testGetCustomersByName() throws Exception {
        List<CustomerResponse> customers = Collections.singletonList(customerResponse);
        when(customerService.getCustomersByName("John")).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/customers?name=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(customerService).getCustomersByName("John");
    }

    @Test
    public void testGetCustomerByEmail() throws Exception {

        List<CustomerResponse> customers = Collections.singletonList(customerResponse);
        when(customerService.getCustomerByEmail("john.doe@example.com")).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/customers?email=john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(customerService).getCustomerByEmail("john.doe@example.com");
    }

    @Test
    public void testGetCustomerById() throws Exception {

        when(customerService.getCustomer("1")).thenReturn(customerResponse);

        // Act & Assert
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService).getCustomer("1");
    }

    @Test
    public void testSaveCustomer() throws Exception {
        // Arrange
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("John Doe");
        customerRequest.setEmail("john.doe@example.com");

        when(customerService.saveCustomer(any(CustomerRequest.class))).thenReturn(customerResponse);

        // Act & Assert
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/customers/1"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService).saveCustomer(any(CustomerRequest.class));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        // Arrange
        CustomerResponse updateCustomerResponse = CustomerResponse.builder()
                .id ("1")
                .name("Updated Name")
                .email("updated.email@example.com").build ();
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Updated Name");
        customerRequest.setEmail("updated.email@example.com");

        when(customerService.updateCustomer(eq("1"), any(CustomerRequest.class))).thenReturn(updateCustomerResponse);

        // Act & Assert
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"));

        verify(customerService).updateCustomer(eq("1"), any(CustomerRequest.class));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        // Arrange
        doNothing().when(customerService).deleteCustomer("1");

        // Act & Assert
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer("1");
    }

}
