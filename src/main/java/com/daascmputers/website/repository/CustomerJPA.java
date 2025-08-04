package com.daascmputers.website.repository;

import com.daascmputers.website.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerJPA extends JpaRepository<Customer, Integer> {

}
