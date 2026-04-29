package com.erp.employee_system.model;

import com.erp.employee_system.model.enums.PayrollStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "payrolls",
        uniqueConstraints = @UniqueConstraint(name = "uk_payroll_month_year", columnNames = {"payroll_month", "payroll_year"})
)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payroll_month", nullable = false)
    private Integer month;

    @Column(name = "payroll_year", nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PayrollStatus status;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @OneToMany(mappedBy = "payroll")
    private List<Payslip> payslips = new ArrayList<>();
}
