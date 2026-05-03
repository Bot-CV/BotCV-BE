package org.toanehihi.botcv.application.token.service;

public interface TokenService {
    void addToBlacklist(String token, long expiryTime);

    boolean isBlacklisted(String token);

    void storeResetToken(String token, String email);

    String getResetTokenEmail(String token);

    void deleteResetToken(String token);

    void storeVerificationToken(String token, String email);

    String getVerificationTokenEmail(String token);

    void deleteVerificationToken(String token);
}
