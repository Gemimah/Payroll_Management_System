package com.erp.employee_system.service;

import com.erp.employee_system.dto.deduction.CreateDeductionRateRequest;
import com.erp.employee_system.dto.deduction.DeductionRateResponse;
import com.erp.employee_system.dto.deduction.UpdateDeductionRateRequest;
import com.erp.employee_system.exception.ConflictException;
import com.erp.employee_system.exception.ResourceNotFoundException;
import com.erp.employee_system.model.DeductionRate;
import com.erp.employee_system.repository.DeductionRateRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeductionRateService {

    private final DeductionRateRepository deductionRateRepository;

    public DeductionRateService(DeductionRateRepository deductionRateRepository) {
        this.deductionRateRepository = deductionRateRepository;
    }

    public DeductionRateResponse create(CreateDeductionRateRequest request) {
        if (deductionRateRepository.findByName(request.name()).isPresent()) {
            throw new ConflictException("Deduction rate name already exists");
        }

        DeductionRate rate = new DeductionRate();
        rate.setName(request.name().trim());
        rate.setCategory(request.category());
        rate.setPercentage(request.percentage());
        rate.setActive(request.active());
        return toResponse(deductionRateRepository.save(rate));
    }

    public List<DeductionRateResponse> getAll() {
        return deductionRateRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DeductionRateResponse update(Long id, UpdateDeductionRateRequest request) {
        DeductionRate rate = deductionRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction rate not found"));

        deductionRateRepository.findByName(request.name().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Deduction rate name already exists");
                });

        rate.setName(request.name().trim());
        rate.setCategory(request.category());
        rate.setPercentage(request.percentage());
        rate.setActive(request.active());
        return toResponse(deductionRateRepository.save(rate));
    }

    public void delete(Long id) {
        DeductionRate rate = deductionRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction rate not found"));
        deductionRateRepository.delete(rate);
    }

    private DeductionRateResponse toResponse(DeductionRate rate) {
        return new DeductionRateResponse(
                rate.getId(),
                rate.getName(),
                rate.getCategory(),
                rate.getPercentage(),
                rate.isActive()
        );
    }
}
