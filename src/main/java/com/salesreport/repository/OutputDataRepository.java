package com.salesreport.repository;

import com.salesreport.converter.TransformedInputData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutputDataRepository extends JpaRepository<TransformedInputData, Long> {
}
