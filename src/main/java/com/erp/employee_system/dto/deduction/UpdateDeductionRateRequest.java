package com.erp.employee_system.dto.deduction;

import com.erp.employee_system.model.enums.DeductionCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateDeductionRateRequest(
        @NotBlank String name,
        @NotNull DeductionCategory category,
        @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal percentage,
        boolean active
) {
}
