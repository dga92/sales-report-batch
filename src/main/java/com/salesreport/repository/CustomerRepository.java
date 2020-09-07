package com.salesreport.repository;

import com.salesreport.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT count(distinct c.cpf) FROM Customer c")
    long totalUniqueCustomers();
}
