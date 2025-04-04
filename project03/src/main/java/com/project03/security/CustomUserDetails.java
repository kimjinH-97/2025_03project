package com.project03.security;

import com.project03.domain.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Employee employee;

    public CustomUserDetails(Employee employee) {
        this.employee = employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 없음
    }

    @Override
    public String getPassword() {
        System.out.println("[로그] DB에 저장된 암호화된 비밀번호: " + employee.getPassword()); //  이 줄 추가
        return employee.getPassword();
    }


    @Override
    public String getUsername() {
        return employee.getUserId(); // 로그인에 사용할 아이디
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public String getUserName() {
        return employee.getUserName();
    }
}
