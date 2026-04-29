package com.erp.employee_system.repository;

import com.erp.employee_system.model.Payroll;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Optional<Payroll> findByMonthAndYear(Integer month, Integer year);
}
