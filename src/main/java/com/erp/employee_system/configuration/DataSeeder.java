package com.erp.employee_system.configuration;

import com.erp.employee_system.model.Employee;
import com.erp.employee_system.model.Employment;
import com.erp.employee_system.model.Role;
import com.erp.employee_system.model.UserAccount;
import com.erp.employee_system.model.DeductionRate;
import com.erp.employee_system.model.enums.DeductionCategory;
import com.erp.employee_system.model.enums.EmploymentStatus;
import com.erp.employee_system.model.enums.RoleName;
import com.erp.employee_system.model.enums.UserStatus;
import com.erp.employee_system.repository.DeductionRateRepository;
import com.erp.employee_system.repository.EmployeeRepository;
import com.erp.employee_system.repository.RoleRepository;
import com.erp.employee_system.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            RoleRepository roleRepository,
            EmployeeRepository employeeRepository,
            UserAccountRepository userAccountRepository,
            DeductionRateRepository deductionRateRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByName(roleName).orElseGet(() -> {
                    Role r = new Role();
                    r.setName(roleName);
                    return roleRepository.save(r);
                });
            }

            seedUserIfMissing(
                    "manager@erp.local",
                    "Manager",
                    "User",
                    "Kigali",
                    "0780000001",
                    "EMP-0001",
                    "HR",
                    "Manager",
                    new BigDecimal("700000.00"),
                    RoleName.ROLE_MANAGER,
                    employeeRepository,
                    userAccountRepository,
                    roleRepository,
                    passwordEncoder
            );

            seedUserIfMissing(
                    "admin@erp.local",
                    "Admin",
                    "User",
                    "Kigali",
                    "0780000002",
                    "EMP-0002",
                    "Finance",
                    "Admin",
                    new BigDecimal("800000.00"),
                    RoleName.ROLE_ADMIN,
                    employeeRepository,
                    userAccountRepository,
                    roleRepository,
                    passwordEncoder
            );

            seedUserIfMissing(
                    "employee@erp.local",
                    "Employee",
                    "User",
                    "Kigali",
                    "0780000003",
                    "EMP-0003",
                    "IT",
                    "Engineer",
                    new BigDecimal("500000.00"),
                    RoleName.ROLE_EMPLOYEE,
                    employeeRepository,
                    userAccountRepository,
                    roleRepository,
                    passwordEncoder
            );

            seedDeductionRateIfMissing("EmployeeTax", DeductionCategory.DEDUCTION, new BigDecimal("30.00"), deductionRateRepository);
            seedDeductionRateIfMissing("Pansion", DeductionCategory.DEDUCTION, new BigDecimal("6.00"), deductionRateRepository);
            seedDeductionRateIfMissing("MedicalInsurance", DeductionCategory.DEDUCTION, new BigDecimal("5.00"), deductionRateRepository);
            seedDeductionRateIfMissing("Others", DeductionCategory.DEDUCTION, new BigDecimal("5.00"), deductionRateRepository);
            seedDeductionRateIfMissing("House", DeductionCategory.ALLOWANCE, new BigDecimal("14.00"), deductionRateRepository);
            seedDeductionRateIfMissing("Transport", DeductionCategory.ALLOWANCE, new BigDecimal("14.00"), deductionRateRepository);
        };
    }

    private static void seedUserIfMissing(
            String email,
            String firstName,
            String lastName,
            String district,
            String mobile,
            String employeeCode,
            String department,
            String position,
            BigDecimal baseSalary,
            RoleName roleName,
            EmployeeRepository employeeRepository,
            UserAccountRepository userAccountRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        if (userAccountRepository.findByUsername(email).isPresent()) {
            return;
        }

        Role role = roleRepository.findByName(roleName).orElseThrow();

        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setDistrict(district);
        employee.setMobile(mobile);
        employee.setDateOfBirth(LocalDate.of(1999, 1, 1));

        Employment employment = new Employment();
        employment.setEmployeeCode(employeeCode);
        employment.setDepartment(department);
        employment.setPosition(position);
        employment.setBaseSalary(baseSalary);
        employment.setStatus(EmploymentStatus.ACTIVE);
        employment.setJoiningDate(LocalDate.now().minusYears(1));
        employment.setEmployee(employee);
        employee.setEmployment(employment);

        UserAccount user = new UserAccount();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode("Password@123"));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmployee(employee);
        user.getRoles().add(role);
        employee.setUserAccount(user);

        employeeRepository.save(employee);
    }

    private static void seedDeductionRateIfMissing(
            String name,
            DeductionCategory category,
            BigDecimal percentage,
            DeductionRateRepository deductionRateRepository
    ) {
        if (deductionRateRepository.findByName(name).isPresent()) {
            return;
        }

        DeductionRate rate = new DeductionRate();
        rate.setName(name);
        rate.setCategory(category);
        rate.setPercentage(percentage);
        rate.setActive(true);
        deductionRateRepository.save(rate);
    }
}

