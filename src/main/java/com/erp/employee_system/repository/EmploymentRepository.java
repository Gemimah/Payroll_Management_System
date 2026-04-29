package com.erp.employee_system.repository;

import com.erp.employee_system.model.Employment;
import com.erp.employee_system.model.enums.EmploymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    Optional<Employment> findByEmployeeCode(String employeeCode);

    List<Employment> findByStatus(EmploymentStatus status);
}
