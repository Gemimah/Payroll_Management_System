package com.erp.employee_system.model;

import com.erp.employee_system.model.enums.PayslipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "payslips",
        uniqueConstraints = @UniqueConstraint(name = "uk_payslip_employee_payroll", columnNames = {"employee_id", "payroll_id"})
)
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal houseAllowance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal transportAllowance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal employeeTax;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pension;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal medicalInsurance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal otherDeductions;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayslipStatus status;
}
