package com.jjeonghee.springboot3develop.springbootdeveloper.service;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.RefreshToken;
import com.jjeonghee.springboot3develop.springbootdeveloper.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("Unexpected Token"));
    }
}
