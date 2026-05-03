package org.toanehihi.botcv.application.token.service;

import org.toanehihi.botcv.domain.model.Account;

public interface JwtService {
    String generateAccessToken(Account account);

    String generateRefreshToken(Account account);

    String extractEmail(String token);

    boolean validateToken(String token);

    boolean isRefreshToken(String token);

    boolean isTokenExpired(String token);

    long getTokenExpiryDuration(String token);

    void blacklistToken(String token);
}
