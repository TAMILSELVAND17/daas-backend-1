package com.daascmputers.website.service;

import com.daascmputers.website.entities.Customer;

import java.util.concurrent.CompletableFuture;

public interface CustomerService {

    CompletableFuture<Customer> customerInfo(Customer customer);

}

