package com.erp.employee_system.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee & Payroll Management API")
                        .description("REST API for employee management, deductions, payroll processing, approval, and messaging.")
                        .version("v1")
                        .contact(new Contact()
                                .name("ERP Backend Team")
                                .email("support@erp.local"))
                        .license(new License()
                                .name("Academic Project")
                                .url("https://example.com/license")));
    }
}
