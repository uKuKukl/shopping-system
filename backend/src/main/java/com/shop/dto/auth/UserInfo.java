package com.shop.dto.auth;

import com.shop.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String username;
    private UserRole role;
}
