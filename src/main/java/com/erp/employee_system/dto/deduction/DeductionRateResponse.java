package com.erp.employee_system.dto.deduction;

import com.erp.employee_system.model.enums.DeductionCategory;
import java.math.BigDecimal;

public record DeductionRateResponse(
        Long id,
        String name,
        DeductionCategory category,
        BigDecimal percentage,
        boolean active
) {
}
