package com.erp.employee_system.service;

import com.erp.employee_system.dto.message.MessageResponse;
import com.erp.employee_system.exception.ResourceNotFoundException;
import com.erp.employee_system.model.Employee;
import com.erp.employee_system.model.Message;
import com.erp.employee_system.model.UserAccount;
import com.erp.employee_system.repository.MessageRepository;
import com.erp.employee_system.repository.UserAccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserAccountRepository userAccountRepository;

    public MessageService(MessageRepository messageRepository, UserAccountRepository userAccountRepository) {
        this.messageRepository = messageRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<MessageResponse> getMyMessages(String username) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        return messageRepository.findByEmployeeId(account.getEmployee().getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MessageResponse> getMessagesForPayroll(Long payrollId) {
        return messageRepository.findByPayslipPayrollId(payrollId).stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        Employee employee = message.getEmployee();
        return new MessageResponse(
                message.getId(),
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                message.getPayslip().getId(),
                message.getMonth(),
                message.getYear(),
                message.getAmount(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
