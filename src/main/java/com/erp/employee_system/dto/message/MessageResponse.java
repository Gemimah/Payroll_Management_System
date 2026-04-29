package com.erp.employee_system.dto.message;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long employeeId,
        String employeeName,
        Long payslipId,
        Integer month,
        Integer year,
        BigDecimal amount,
        String content,
        LocalDateTime sentAt
) {
}
