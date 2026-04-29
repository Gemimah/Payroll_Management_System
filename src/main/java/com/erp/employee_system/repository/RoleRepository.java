package com.erp.employee_system.repository;

import com.erp.employee_system.model.Role;
import com.erp.employee_system.model.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
