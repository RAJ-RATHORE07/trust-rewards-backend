package com.fintech.auth.service;

import com.fintech.auth.dto.*;
import com.fintech.auth.entity.*;
import com.fintech.auth.repository.*;
import com.fintech.auth.security.*;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public AuthService(
            UserRepository userRepo,
            RefreshTokenRepository refreshRepo,
            PasswordEncoder encoder,
            JwtUtil jwtUtil,
            TokenBlacklistService blacklistService) {

        this.userRepo = userRepo;
        this.refreshRepo = refreshRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    /* ================= REGISTER ================= */

    public void register(RegisterRequest req) {
        if (userRepo.findByEmail(req.email()).isPresent())
            throw new RuntimeException("Email already exists");

        User user = new User();
        user.setEmail(req.email());
        user.setPassword(encoder.encode(req.password()));
        user.setRole("USER");

        userRepo.save(user);
    }

    /* ================= LOGIN ================= */

    public AuthResponse login(LoginRequest req) {

        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        return issueTokens(user);
    }

    /* ================= REFRESH (FIXED) ================= */

    public AuthResponse refresh(String refreshToken) {

        Claims claims = jwtUtil.parseToken(refreshToken);

        if (!jwtUtil.isRefreshToken(claims))
            throw new RuntimeException("Invalid refresh token");

        UUID userId = UUID.fromString(claims.getSubject());

        // 🔍 Must exist
        RefreshToken stored = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token revoked"));

        // 🔁 rotate
        refreshRepo.delete(stored);

        long ttl = jwtUtil.getRemainingValidity(claims);
        blacklistService.blacklist(refreshToken, ttl);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return issueTokens(user);
    }

    /* ================= LOGOUT (FIXED) ================= */

    @Transactional
    public void logout(String refreshToken) {

        Claims claims = jwtUtil.parseToken(refreshToken);

        if (!jwtUtil.isRefreshToken(claims))
            throw new RuntimeException("Invalid refresh token");

        // blacklist refresh token
        long ttl = jwtUtil.getRemainingValidity(claims);
        blacklistService.blacklist(refreshToken, ttl);

        refreshRepo.findByToken(refreshToken)
                .ifPresent(refreshRepo::delete);
    }
    /* ================= TOKEN ISSUER ================= */

    @Transactional
    private AuthResponse issueTokens(User user) {

        refreshRepo.deleteByUserId(user.getId());

        String access = jwtUtil.generateAccessToken(
                user.getId(), user.getRole());

        String refresh = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken token = new RefreshToken();
        token.setToken(refresh);
        token.setUserId(user.getId());
        token.setExpiryDate(
                new java.util.Date(System.currentTimeMillis() + 7 * 86400000L));

        refreshRepo.save(token);

        return new AuthResponse(access, refresh);
    }

}
