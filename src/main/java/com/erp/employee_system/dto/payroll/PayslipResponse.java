package com.erp.employee_system.dto.payroll;

import com.erp.employee_system.model.enums.PayslipStatus;
import java.math.BigDecimal;

public record PayslipResponse(
        Long id,
        Long employeeId,
        String employeeName,
        String employeeCode,
        Integer month,
        Integer year,
        BigDecimal baseSalary,
        BigDecimal houseAllowance,
        BigDecimal transportAllowance,
        BigDecimal grossSalary,
        BigDecimal employeeTax,
        BigDecimal pension,
        BigDecimal medicalInsurance,
        BigDecimal otherDeductions,
        BigDecimal netSalary,
        PayslipStatus status
) {
}
