package com.erp.employee_system.service;

import com.erp.employee_system.model.UserAccount;
import com.erp.employee_system.model.enums.UserStatus;
import com.erp.employee_system.repository.UserAccountRepository;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(user.getStatus() == null || user.getStatus() == UserStatus.INACTIVE)
                .authorities(
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}

