package com.erp.employee_system.dto.payroll;

import com.erp.employee_system.model.enums.PayrollStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayrollSummaryResponse(
        Long id,
        Integer month,
        Integer year,
        PayrollStatus status,
        LocalDateTime processedAt,
        Integer totalPayslips,
        BigDecimal totalNetSalary
) {
}
