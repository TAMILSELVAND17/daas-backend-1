package com.daascmputers.website.serviceimpl;

import com.daascmputers.website.entities.Customer;
import com.daascmputers.website.exceptionhandler.Exceptions;
import com.daascmputers.website.repository.CustomerJPA;
import com.daascmputers.website.service.CustomerService;
import com.daascmputers.website.tools.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class CustomerImp implements CustomerService {

    @Autowired
    private CustomerJPA customerJPA;

    @Autowired
    private EmailService emailService;

    private Customer findTheCustomer(int id) {
        return customerJPA.findById(id)
                .orElseThrow(() -> new Exceptions("Customer not found with id: " + id));
    }


    @Override
    @Transactional
    @Async
    public CompletableFuture<Customer> customerInfo(Customer customer) {
        customerJPA.save(customer);
        Customer saved = findTheCustomer(customer.getCustomerID());

        String subject = "New Customer Inquiry: " + saved.getName().toUpperCase();
        String body = "You've received a new message from " + saved.getName() + "\n\n" +
                "Email: " + saved.getEmail() + "\n" +
                "Mobile: " + saved.getMobile() + "\n" +
                "Message: " + saved.getMessage();

        emailService.sendEmail(subject, body);

        String userSubject = "Thank You for Contacting Daas Computers";
        String userBody = "Dear " + saved.getName() + ",\n\n" +
                "Thank you for reaching out to Daas Computers. We have received your inquiry and will get back to you shortly.\n\n" +
                "Here is a copy of your message:\n" +
                saved.getMessage() + "\n\n" +
                "Regards,\nDaas Computers Team";

        emailService.sendEmail(saved.getEmail(), userSubject, userBody);

        return CompletableFuture.completedFuture(saved);
    }
}
