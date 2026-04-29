package com.erp.employee_system.repository;

import com.erp.employee_system.model.Payslip;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    Optional<Payslip> findByEmployeeIdAndPayrollId(Long employeeId, Long payrollId);

    List<Payslip> findByPayrollId(Long payrollId);

    List<Payslip> findByEmployeeId(Long employeeId);
}
