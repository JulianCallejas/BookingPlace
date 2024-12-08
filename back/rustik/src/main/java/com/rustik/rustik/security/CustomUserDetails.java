package com.rustik.rustik.security;

import com.rustik.rustik.model.User;
import com.rustik.rustik.model.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails, Principal {

    @Getter
    private User user;

    @Getter
    @Setter
    private String token;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.token = null;
        this.authorities = Collections.emptyList();
    }

    public CustomUserDetails(User user, String token, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.token = token;
        this.authorities = authorities;
    }
    public CustomUserDetails(User user,  Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.token = token;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }


    public Boolean getIsAdmin(){
        return user.getRole().equals(UserRole.ROLE_ADMIN);
    }


    @Override
    public String getName() {
        return "";
    }
}
