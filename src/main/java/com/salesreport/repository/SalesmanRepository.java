package com.salesreport.repository;

import com.salesreport.model.Salesman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesmanRepository extends JpaRepository<Salesman, Long> {
    @Query("SELECT count(distinct s.cnpj) FROM Salesman s")
    long totalUniqueSalesman();
}
