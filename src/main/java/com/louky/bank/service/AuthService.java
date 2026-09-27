package com.louky.bank.service;

import com.louky.bank.dto.request.LoginRequest;
import com.louky.bank.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    void logout(String refreshToken);
}