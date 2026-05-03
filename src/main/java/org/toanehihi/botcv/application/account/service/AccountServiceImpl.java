package org.toanehihi.botcv.application.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.toanehihi.botcv.application.email.service.EmailService;
import org.toanehihi.botcv.application.token.service.TokenService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.domain.model.enums.AccountStatus;
import org.toanehihi.botcv.infrastructure.persistence.repositories.AccountRepository;
import org.toanehihi.botcv.interfaces.web.dtos.account.ForgotPasswordRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.ResendVerificationRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.ResetPasswordRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account findByEmailOrThrow(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long accountId) {
        return accountRepository.existsById(accountId);
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void changeAccountStatus(Long accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus(status);
        accountRepository.save(account);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        String resetToken = UUID.randomUUID().toString();
        tokenService.storeResetToken(resetToken, account.getEmail());
        emailService.sendPasswordResetEmail(account.getEmail(), resetToken);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = tokenService.getResetTokenEmail(request.getToken());
        if (email == null) {
            throw new AppException(ErrorCode.AUTH_RESET_TOKEN_INVALID);
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (passwordEncoder.matches(request.getNewPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
        tokenService.deleteResetToken(request.getToken());
    }

    @Override
    public void resendVerificationEmail(ResendVerificationRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getVerifiedAt() != null) {
            throw new AppException(ErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }
        String verificationToken = UUID.randomUUID().toString();
        tokenService.storeVerificationToken(verificationToken, account.getEmail());
        emailService.sendVerificationEmail(account.getEmail(), verificationToken);
        log.info("Verification email resent to: {}", account.getEmail());
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        String email = tokenService.getVerificationTokenEmail(token);
        if (email == null) {
            throw new AppException(ErrorCode.ACCOUNT_VERIFY_TOKEN_INVALID);
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setVerifiedAt(OffsetDateTime.now());
        accountRepository.save(account);
        tokenService.deleteVerificationToken(token);
        log.info("Email verified successfully for: {}", email);
    }
}
