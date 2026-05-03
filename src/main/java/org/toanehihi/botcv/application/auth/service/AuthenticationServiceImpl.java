package org.toanehihi.botcv.application.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.toanehihi.botcv.application.account.service.AccountService;
import org.toanehihi.botcv.application.token.service.JwtService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.domain.model.enums.AccountStatus;
import org.toanehihi.botcv.infrastructure.security.AccountUserDetails;
import org.toanehihi.botcv.interfaces.web.dtos.auth.AuthenticationResponse;
import org.toanehihi.botcv.interfaces.web.dtos.auth.GoogleLoginRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.LoginRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        Account existedAccount = accountService.findByEmailOrThrow(request.getEmail());

        if (existedAccount.getPassword() == null) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
            Account account = userDetails.getAccount();

            if (account.getStatus() == AccountStatus.SUSPENDED) {
                throw new AppException(ErrorCode.AUTH_ACCOUNT_SUSPENDED);
            }

            if (account.getVerifiedAt() == null) {
                throw new AppException(ErrorCode.ACCOUNT_VERIFY_TOKEN_INVALID);
            }

            String accessToken = jwtService.generateAccessToken(account);
            String refreshToken = jwtService.generateRefreshToken(account);
            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public AuthenticationResponse loginWithGoogle(GoogleLoginRequest request) {
        throw new UnsupportedOperationException("Google login not yet implemented");
    }
}
