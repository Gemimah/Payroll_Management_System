package com.erp.employee_system.repository;

import com.erp.employee_system.model.DeductionRate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeductionRateRepository extends JpaRepository<DeductionRate, Long> {
    Optional<DeductionRate> findByName(String name);

    List<DeductionRate> findByActiveTrue();
}
