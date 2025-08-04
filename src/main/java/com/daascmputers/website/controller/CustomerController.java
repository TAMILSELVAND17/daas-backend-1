package com.daascmputers.website.controller;


import com.daascmputers.website.entities.Customer;
import com.daascmputers.website.service.CustomerService;
import com.daascmputers.website.utility.ResponseStructure;
import com.daascmputers.website.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/customers")
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin
@AllArgsConstructor
public class CustomerController {
    @Autowired
    private final RestResponseBuilder restResponseBuilder;

    @Autowired
    private CustomerService customerService;


    @PostMapping("/contact")
    public CompletableFuture<ResponseEntity<ResponseStructure<Customer>>> customerInfo(@Valid @RequestBody Customer customer) {
        CompletableFuture<Customer> future = customerService.customerInfo(customer);

        return future.thenApply(saved -> restResponseBuilder.success(HttpStatus.CREATED, "Customer Details Sent SuccessFullly", saved))
                .exceptionally(exception -> {
                    ResponseStructure<Customer> errorResponse = new ResponseStructure<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failed to save the Customer Details: " + exception.getMessage(),
                            null
                    );
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                });
    }
}