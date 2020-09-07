package com.salesreport.repository;

import com.salesreport.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query(value = "SELECT max(si.value), * FROM sales s JOIN sales_item si on si.sales_obj_id=s.obj_id GROUP BY s.sale_id, si.value ORDER BY si.value DESC LIMIT 1", nativeQuery = true)
    Sales findMostExpensive();

    @Query(value = "SELECT sum(si.value), * FROM sales s JOIN sales_item si on si.sales_obj_id=s.obj_id GROUP BY s.sale_id, si.value ORDER BY si.value ASC LIMIT 1", nativeQuery = true)
    Sales findWorstSellers();

}
