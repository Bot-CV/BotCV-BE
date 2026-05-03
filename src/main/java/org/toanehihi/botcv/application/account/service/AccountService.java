package org.toanehihi.botcv.application.account.service;

import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.domain.model.enums.AccountStatus;
import org.toanehihi.botcv.interfaces.web.dtos.account.ForgotPasswordRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.ResendVerificationRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.ResetPasswordRequest;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findByEmail(String email);

    Account findByEmailOrThrow(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long accountId);

    Account save(Account account);

    void resendVerificationEmail(ResendVerificationRequest request);

    void verifyEmail(String token);

    void changeAccountStatus(Long accountId, AccountStatus status);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
