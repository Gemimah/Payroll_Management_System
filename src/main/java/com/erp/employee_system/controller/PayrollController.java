package com.erp.employee_system.controller;

import com.erp.employee_system.dto.payroll.ApprovePayrollRequest;
import com.erp.employee_system.dto.payroll.PayslipResponse;
import com.erp.employee_system.dto.payroll.PayrollSummaryResponse;
import com.erp.employee_system.dto.payroll.ProcessPayrollRequest;
import com.erp.employee_system.service.PayrollService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    public PayrollSummaryResponse processPayroll(@Valid @RequestBody ProcessPayrollRequest request) {
        return payrollService.processPayroll(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<PayrollSummaryResponse> getPayrolls() {
        return payrollService.getPayrolls();
    }

    @GetMapping("/{payrollId}/payslips")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<PayslipResponse> getPayslipsForPayroll(@PathVariable Long payrollId) {
        return payrollService.getPayslipsForPayroll(payrollId);
    }

    @PostMapping("/{payrollId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public PayrollSummaryResponse approvePayroll(
            @PathVariable Long payrollId,
            @Valid @RequestBody ApprovePayrollRequest request
    ) {
        return payrollService.approvePayroll(payrollId, request);
    }

    @GetMapping("/me/payslips")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<PayslipResponse> getMyPayslips(Authentication authentication) {
        return payrollService.getMyPayslips(authentication.getName());
    }
}
