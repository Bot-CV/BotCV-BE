package org.toanehihi.jobrecruitmentplatformserver.application.email.service;

import org.springframework.lang.Nullable;

public interface EmailService {
    void sendPasswordResetEmail(String recieveEmail, String token);

    void sendVerificationEmail(String receiveEmail, String token);

    void sendCompanyVerificationResult(String receiveEmail, boolean isApproved, @Nullable String reason);
}
