package com.erp.employee_system.dto.payroll;

import jakarta.validation.constraints.NotBlank;

public record ApprovePayrollRequest(@NotBlank String institutionName) {
}
