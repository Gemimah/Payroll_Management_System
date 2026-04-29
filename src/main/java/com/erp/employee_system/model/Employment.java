package com.erp.employee_system.model;

import com.erp.employee_system.model.enums.EmploymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employments")
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String employeeCode;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentStatus status;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;
}
