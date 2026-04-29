package com.erp.employee_system.service;

import com.erp.employee_system.dto.employee.CreateEmployeeRequest;
import com.erp.employee_system.dto.employee.EmployeeResponse;
import com.erp.employee_system.exception.ConflictException;
import com.erp.employee_system.exception.ResourceNotFoundException;
import com.erp.employee_system.model.Employee;
import com.erp.employee_system.model.Employment;
import com.erp.employee_system.model.Role;
import com.erp.employee_system.model.UserAccount;
import com.erp.employee_system.model.enums.RoleName;
import com.erp.employee_system.model.enums.UserStatus;
import com.erp.employee_system.repository.EmployeeRepository;
import com.erp.employee_system.repository.EmploymentRepository;
import com.erp.employee_system.repository.RoleRepository;
import com.erp.employee_system.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            EmploymentRepository employmentRepository,
            UserAccountRepository userAccountRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.employmentRepository = employmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Employee email already exists");
        }
        if (employmentRepository.findByEmployeeCode(request.employeeCode()).isPresent()) {
            throw new ConflictException("Employee code already exists");
        }
        if (userAccountRepository.findByUsername(request.email()).isPresent()) {
            throw new ConflictException("User account already exists for this email");
        }

        Employee employee = new Employee();
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setDistrict(request.district());
        employee.setMobile(request.mobile());
        employee.setDateOfBirth(request.dateOfBirth());

        Employment employment = new Employment();
        employment.setEmployeeCode(request.employeeCode());
        employment.setDepartment(request.department());
        employment.setPosition(request.position());
        employment.setBaseSalary(request.baseSalary());
        employment.setStatus(request.status());
        employment.setJoiningDate(request.joiningDate());
        employment.setEmployee(employee);
        employee.setEmployment(employment);

        Role employeeRole = roleRepository.findByName(RoleName.ROLE_EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException("Employee role not configured"));

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(request.email());
        userAccount.setPassword(passwordEncoder.encode(request.password()));
        userAccount.setStatus(UserStatus.ACTIVE);
        userAccount.setEmployee(employee);
        userAccount.getRoles().add(employeeRole);
        employee.setUserAccount(userAccount);

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return toResponse(employee);
    }

    private EmployeeResponse toResponse(Employee employee) {
        Employment employment = employee.getEmployment();
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDistrict(),
                employee.getMobile(),
                employee.getDateOfBirth(),
                employment != null ? employment.getEmployeeCode() : null,
                employment != null ? employment.getDepartment() : null,
                employment != null ? employment.getPosition() : null,
                employment != null ? employment.getBaseSalary() : null,
                employment != null ? employment.getStatus() : null,
                employment != null ? employment.getJoiningDate() : null
        );
    }
}
