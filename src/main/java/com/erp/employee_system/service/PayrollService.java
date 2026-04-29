package com.erp.employee_system.service;

import com.erp.employee_system.dto.payroll.ApprovePayrollRequest;
import com.erp.employee_system.dto.payroll.PayslipResponse;
import com.erp.employee_system.dto.payroll.PayrollSummaryResponse;
import com.erp.employee_system.dto.payroll.ProcessPayrollRequest;
import com.erp.employee_system.exception.ConflictException;
import com.erp.employee_system.exception.ResourceNotFoundException;
import com.erp.employee_system.model.DeductionRate;
import com.erp.employee_system.model.Employee;
import com.erp.employee_system.model.Employment;
import com.erp.employee_system.model.Message;
import com.erp.employee_system.model.Payslip;
import com.erp.employee_system.model.Payroll;
import com.erp.employee_system.model.UserAccount;
import com.erp.employee_system.model.enums.EmploymentStatus;
import com.erp.employee_system.model.enums.PayslipStatus;
import com.erp.employee_system.model.enums.PayrollStatus;
import com.erp.employee_system.repository.DeductionRateRepository;
import com.erp.employee_system.repository.EmploymentRepository;
import com.erp.employee_system.repository.MessageRepository;
import com.erp.employee_system.repository.PayslipRepository;
import com.erp.employee_system.repository.PayrollRepository;
import com.erp.employee_system.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PayrollService {

    private static final String EMPLOYEE_TAX = "employeetax";
    private static final String PENSION = "pansion";
    private static final String MEDICAL_INSURANCE = "medicalinsurance";
    private static final String OTHERS = "others";
    private static final String HOUSE = "house";
    private static final String TRANSPORT = "transport";

    private final PayrollRepository payrollRepository;
    private final PayslipRepository payslipRepository;
    private final DeductionRateRepository deductionRateRepository;
    private final EmploymentRepository employmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final MessageRepository messageRepository;
    private final EmailService emailService;

    public PayrollService(
            PayrollRepository payrollRepository,
            PayslipRepository payslipRepository,
            DeductionRateRepository deductionRateRepository,
            EmploymentRepository employmentRepository,
            UserAccountRepository userAccountRepository,
            MessageRepository messageRepository,
            EmailService emailService
    ) {
        this.payrollRepository = payrollRepository;
        this.payslipRepository = payslipRepository;
        this.deductionRateRepository = deductionRateRepository;
        this.employmentRepository = employmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.messageRepository = messageRepository;
        this.emailService = emailService;
    }

    @Transactional
    public PayrollSummaryResponse processPayroll(ProcessPayrollRequest request) {
        if (payrollRepository.findByMonthAndYear(request.month(), request.year()).isPresent()) {
            throw new ConflictException("Payroll already exists for this month and year");
        }

        Map<String, DeductionRate> ratesByName = deductionRateRepository.findByActiveTrue().stream()
                .collect(Collectors.toMap(
                        rate -> normalizeRateName(rate.getName()),
                        Function.identity(),
                        (left, right) -> right
                ));

        BigDecimal employeeTaxRate = requireRate(ratesByName, EMPLOYEE_TAX);
        BigDecimal pensionRate = requireRate(ratesByName, PENSION);
        BigDecimal medicalRate = requireRate(ratesByName, MEDICAL_INSURANCE);
        BigDecimal othersRate = requireRate(ratesByName, OTHERS);
        BigDecimal houseRate = requireRate(ratesByName, HOUSE);
        BigDecimal transportRate = requireRate(ratesByName, TRANSPORT);

        List<Employment> activeEmployments = employmentRepository.findByStatus(EmploymentStatus.ACTIVE);
        if (activeEmployments.isEmpty()) {
            throw new ConflictException("No active employees found for payroll processing");
        }

        Payroll payroll = new Payroll();
        payroll.setMonth(request.month());
        payroll.setYear(request.year());
        payroll.setStatus(PayrollStatus.PENDING_APPROVAL);
        payroll.setProcessedAt(LocalDateTime.now());
        Payroll savedPayroll = payrollRepository.save(payroll);

        for (Employment employment : activeEmployments) {
            Employee employee = employment.getEmployee();
            BigDecimal baseSalary = employment.getBaseSalary();

            BigDecimal houseAllowance = percentageOf(baseSalary, houseRate);
            BigDecimal transportAllowance = percentageOf(baseSalary, transportRate);
            BigDecimal grossSalary = baseSalary.add(houseAllowance).add(transportAllowance).setScale(2, RoundingMode.HALF_UP);

            BigDecimal employeeTax = percentageOf(baseSalary, employeeTaxRate);
            BigDecimal pension = percentageOf(baseSalary, pensionRate);
            BigDecimal medicalInsurance = percentageOf(baseSalary, medicalRate);
            BigDecimal otherDeductions = percentageOf(baseSalary, othersRate);
            BigDecimal totalDeductions = employeeTax.add(pension).add(medicalInsurance).add(otherDeductions)
                    .setScale(2, RoundingMode.HALF_UP);

            if (totalDeductions.compareTo(grossSalary) > 0) {
                throw new ConflictException("Total deductions exceed gross salary for employee " + employee.getId());
            }

            BigDecimal netSalary = grossSalary.subtract(totalDeductions).setScale(2, RoundingMode.HALF_UP);

            Payslip payslip = new Payslip();
            payslip.setEmployee(employee);
            payslip.setPayroll(savedPayroll);
            payslip.setBaseSalary(baseSalary.setScale(2, RoundingMode.HALF_UP));
            payslip.setHouseAllowance(houseAllowance);
            payslip.setTransportAllowance(transportAllowance);
            payslip.setGrossSalary(grossSalary);
            payslip.setEmployeeTax(employeeTax);
            payslip.setPension(pension);
            payslip.setMedicalInsurance(medicalInsurance);
            payslip.setOtherDeductions(otherDeductions);
            payslip.setNetSalary(netSalary);
            payslip.setStatus(PayslipStatus.PENDING);
            payslipRepository.save(payslip);
        }

        return toPayrollSummary(savedPayroll);
    }

    public List<PayrollSummaryResponse> getPayrolls() {
        return payrollRepository.findAll().stream()
                .map(this::toPayrollSummary)
                .toList();
    }

    public List<PayslipResponse> getPayslipsForPayroll(Long payrollId) {
        payrollRepository.findById(payrollId).orElseThrow(() -> new ResourceNotFoundException("Payroll not found"));
        return payslipRepository.findByPayrollId(payrollId).stream()
                .map(this::toPayslipResponse)
                .toList();
    }

    public List<PayslipResponse> getMyPayslips(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        return payslipRepository.findByEmployeeId(account.getEmployee().getId()).stream()
                .map(this::toPayslipResponse)
                .toList();
    }

    @Transactional
    public PayrollSummaryResponse approvePayroll(Long payrollId, ApprovePayrollRequest request) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found"));

        if (payroll.getStatus() == PayrollStatus.APPROVED) {
            throw new ConflictException("Payroll is already approved");
        }

        List<Payslip> payslips = payslipRepository.findByPayrollId(payrollId);
        for (Payslip payslip : payslips) {
            payslip.setStatus(PayslipStatus.PAID);
            payslipRepository.save(payslip);

            Message message = new Message();
            message.setEmployee(payslip.getEmployee());
            message.setPayslip(payslip);
            message.setMonth(payroll.getMonth());
            message.setYear(payroll.getYear());
            message.setAmount(payslip.getNetSalary());
            message.setContent(formatMessage(
                    payslip.getEmployee().getFirstName(),
                    payslip.getNetSalary(),
                    payroll.getMonth(),
                    payroll.getYear(),
                    request.institutionName(),
                    payslip.getEmployee().getEmployment().getEmployeeCode()
            ));
            message.setSentAt(LocalDateTime.now());
            messageRepository.save(message);

            emailService.sendSalaryNotification(
                    payslip.getEmployee().getEmail(),
                    "Salary Payment Notification - " + payroll.getMonth() + "/" + payroll.getYear(),
                    message.getContent()
            );
        }

        payroll.setStatus(PayrollStatus.APPROVED);
        payrollRepository.save(payroll);
        return toPayrollSummary(payroll);
    }

    private PayrollSummaryResponse toPayrollSummary(Payroll payroll) {
        List<Payslip> payslips = payslipRepository.findByPayrollId(payroll.getId());
        BigDecimal totalNet = payslips.stream()
                .map(Payslip::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new PayrollSummaryResponse(
                payroll.getId(),
                payroll.getMonth(),
                payroll.getYear(),
                payroll.getStatus(),
                payroll.getProcessedAt(),
                payslips.size(),
                totalNet
        );
    }

    private PayslipResponse toPayslipResponse(Payslip payslip) {
        Employee employee = payslip.getEmployee();
        return new PayslipResponse(
                payslip.getId(),
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getEmployment().getEmployeeCode(),
                payslip.getPayroll().getMonth(),
                payslip.getPayroll().getYear(),
                payslip.getBaseSalary(),
                payslip.getHouseAllowance(),
                payslip.getTransportAllowance(),
                payslip.getGrossSalary(),
                payslip.getEmployeeTax(),
                payslip.getPension(),
                payslip.getMedicalInsurance(),
                payslip.getOtherDeductions(),
                payslip.getNetSalary(),
                payslip.getStatus()
        );
    }

    private BigDecimal percentageOf(BigDecimal value, BigDecimal percentage) {
        return value.multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal requireRate(Map<String, DeductionRate> ratesByName, String rateName) {
        DeductionRate rate = ratesByName.get(rateName);
        if (rate == null) {
            throw new ConflictException("Missing active deduction/allowance rate: " + rateName);
        }
        return rate.getPercentage();
    }

    private String normalizeRateName(String name) {
        return name == null ? "" : name.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private String formatMessage(
            String firstName,
            BigDecimal amount,
            Integer month,
            Integer year,
            String institution,
            String employeeCode
    ) {
        return "Dear " + firstName + " Your salary of " + month + "/" + year + " from " + institution
                + " " + amount + " has been credited to your " + employeeCode + " account Successfully.";
    }
}
