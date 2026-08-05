package com.pm.authservice.security;

import com.pm.authservice.config.JwtProperties;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.entity.Account;
import com.pm.authservice.entity.RefreshToken;
import com.pm.authservice.exception.ErrorCode;
import com.pm.authservice.exception.UnauthorizedException;
import com.pm.authservice.repository.RefreshTokenRepository;
import com.pm.authservice.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;

    @Transactional
    public RefreshToken createRefreshToken(Account account) {
        RefreshToken refreshToken = RefreshToken.builder()
                .account(account)
                .token(UUID.randomUUID().toString())
                .expiresAt(OffsetDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration()))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public AuthResponse rotateRefreshToken(String oldTokenStr) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(oldTokenStr)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_006, "Refresh Token not found or expired"));

        if (oldToken.getRevokedAt() != null) {
            log.warn("Attempted use of revoked refresh token for account {}", oldToken.getAccount().getId());
            // Security measure: Revoke all refresh tokens for this compromised account
            refreshTokenRepository.revokeAllByAccountId(oldToken.getAccount().getId(), OffsetDateTime.now());
            throw new UnauthorizedException(ErrorCode.AUTH_007, "Refresh Token has been revoked");
        }

        if (oldToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new UnauthorizedException(ErrorCode.AUTH_006, "Refresh Token has expired");
        }

        // Revoke the old token
        oldToken.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(oldToken);

        // Generate new Refresh Token and new JWT Access Token
        Account account = oldToken.getAccount();
        RefreshToken newToken = createRefreshToken(account);
        String accessToken = jwtProvider.generateAccessToken(account);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newToken.getToken())
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void revokeRefreshToken(String tokenStr) {
        refreshTokenRepository.findByToken(tokenStr).ifPresent(token -> {
            token.setRevokedAt(OffsetDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void revokeAllByAccountId(UUID accountId) {
        refreshTokenRepository.revokeAllByAccountId(accountId, OffsetDateTime.now());
    }
}
