package com.rustik.rustik.dto;

import com.rustik.rustik.model.User;
import com.rustik.rustik.model.UserRole;
import lombok.Data;
import lombok.Getter;

@Data
public class AuthUserDTO {
    private Long id;

    private String name;

    private String email;

    private String token;

    private Boolean isAdmin;

    public AuthUserDTO(User user, String token){
        this.id = user.getId();
        this.name = user.getName() + " " + user.getSurname();
        this.email = user.getEmail();
        this.isAdmin = user.getRole() == UserRole.ROLE_ADMIN;
        this.token = token;
    }
}
