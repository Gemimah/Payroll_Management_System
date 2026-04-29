package com.erp.employee_system.dto.employee;

import com.erp.employee_system.model.enums.EmploymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String district,
        String mobile,
        LocalDate dateOfBirth,
        String employeeCode,
        String department,
        String position,
        BigDecimal baseSalary,
        EmploymentStatus status,
        LocalDate joiningDate
) {
}
