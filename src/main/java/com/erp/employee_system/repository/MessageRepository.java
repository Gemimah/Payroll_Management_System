package com.erp.employee_system.repository;

import com.erp.employee_system.model.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByEmployeeId(Long employeeId);

    List<Message> findByPayslipPayrollId(Long payrollId);
}
