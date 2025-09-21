package com.ultimate.wellme.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add user's primary role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        if(user.getRole() == User.Role.DOCTOR) {
            // Doctors have both DOCTOR and PATIENT roles
            authorities.add(new SimpleGrantedAuthority("ROLE_" + User.Role.PATIENT.name()));
        } else if(user.getRole() == User.Role.ADMIN) {
            // Admins have ADMIN, DOCTOR and PATIENT roles
            authorities.add(new SimpleGrantedAuthority("ROLE_" + User.Role.DOCTOR.name()));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + User.Role.PATIENT.name()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
