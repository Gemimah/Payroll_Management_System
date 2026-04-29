package com.erp.employee_system.controller;

import com.erp.employee_system.dto.message.MessageResponse;
import com.erp.employee_system.service.MessageService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<MessageResponse> getMyMessages(Authentication authentication) {
        return messageService.getMyMessages(authentication.getName());
    }

    @GetMapping("/payroll/{payrollId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<MessageResponse> getMessagesForPayroll(@PathVariable Long payrollId) {
        return messageService.getMessagesForPayroll(payrollId);
    }
}
