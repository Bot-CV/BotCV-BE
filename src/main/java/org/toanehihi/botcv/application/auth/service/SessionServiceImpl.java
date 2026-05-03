package org.toanehihi.botcv.application.auth.service;

import org.springframework.stereotype.Service;
import org.toanehihi.botcv.application.account.service.AccountService;
import org.toanehihi.botcv.application.token.service.JwtService;
import org.toanehihi.botcv.application.token.service.TokenService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.domain.model.enums.AccountStatus;
import org.toanehihi.botcv.interfaces.web.dtos.auth.AuthenticationResponse;
import org.toanehihi.botcv.interfaces.web.dtos.auth.LogoutRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.RefreshTokenRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AccountService accountService;

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new AppException(ErrorCode.JWT_INVALID_TOKEN);
        }
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new AppException(ErrorCode.JWT_EXPIRED_TOKEN);
        }
        if (!jwtService.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.JWT_INVALID_TOKEN);
        }
        if (tokenService.isBlacklisted(refreshToken)) {
            throw new AppException(ErrorCode.JWT_TOKEN_BLACKLISTED);
        }

        String email = jwtService.extractEmail(refreshToken);
        Account account = accountService.findByEmailOrThrow(email);

        if (account.getStatus() == AccountStatus.SUSPENDED) {
            throw new AppException(ErrorCode.AUTH_ACCOUNT_SUSPENDED);
        }

        String newAccessToken = jwtService.generateAccessToken(account);
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException(ErrorCode.JWT_INVALID_TOKEN);
        }
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new AppException(ErrorCode.JWT_INVALID_TOKEN);
        }
        if (!jwtService.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.JWT_INVALID_TOKEN);
        }
        if (tokenService.isBlacklisted(refreshToken)) {
            log.info("Token already blacklisted");
            return;
        }
        jwtService.blacklistToken(refreshToken);
    }
}
