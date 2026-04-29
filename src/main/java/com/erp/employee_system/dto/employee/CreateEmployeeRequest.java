package com.erp.employee_system.dto.employee;

import com.erp.employee_system.model.enums.EmploymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @NotBlank String district,
        @NotBlank String mobile,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank String employeeCode,
        @NotBlank String department,
        @NotBlank String position,
        @NotNull @DecimalMin("0.0") BigDecimal baseSalary,
        @NotNull EmploymentStatus status,
        @NotNull LocalDate joiningDate,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {
}
