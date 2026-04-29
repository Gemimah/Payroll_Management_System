package com.erp.employee_system.controller;

import com.erp.employee_system.dto.deduction.CreateDeductionRateRequest;
import com.erp.employee_system.dto.deduction.DeductionRateResponse;
import com.erp.employee_system.dto.deduction.UpdateDeductionRateRequest;
import com.erp.employee_system.service.DeductionRateService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deductions")
public class DeductionRateController {

    private final DeductionRateService deductionRateService;

    public DeductionRateController(DeductionRateService deductionRateService) {
        this.deductionRateService = deductionRateService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DeductionRateResponse create(@Valid @RequestBody CreateDeductionRateRequest request) {
        return deductionRateService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<DeductionRateResponse> getAll() {
        return deductionRateService.getAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public DeductionRateResponse update(@PathVariable Long id, @Valid @RequestBody UpdateDeductionRateRequest request) {
        return deductionRateService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        deductionRateService.delete(id);
    }
}
