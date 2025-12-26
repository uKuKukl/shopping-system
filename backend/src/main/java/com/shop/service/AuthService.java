package com.shop.service;

import com.shop.common.BusinessException;
import com.shop.dto.auth.AuthResponse;
import com.shop.dto.auth.LoginRequest;
import com.shop.dto.auth.RegisterRequest;
import com.shop.dto.auth.UserInfo;
import com.shop.entity.User;
import com.shop.enums.UserRole;
import com.shop.enums.UserStatus;
import com.shop.repository.UserRepository;
import com.shop.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("邮箱已存在");
        }
        if (request.getPhone() != null && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new BusinessException("手机号已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, new UserInfo(user.getId(), user.getUsername(), user.getRole()));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("密码错误");
        }
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, new UserInfo(user.getId(), user.getUsername(), user.getRole()));
    }
}
