package com.project03.security;

import com.project03.repository.manufacturing.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println("👉 loadUserByUsername 호출됨: " + userId); // ★ 확인 포인트

        return employeeRepository.findByUserId(userId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> {
                    System.out.println("사용자를 찾을 수 없습니다: " + userId);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });
    }


}
