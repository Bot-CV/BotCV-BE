package org.toanehihi.botcv.application.auth.service;

import org.toanehihi.botcv.interfaces.web.dtos.auth.AuthenticationResponse;
import org.toanehihi.botcv.interfaces.web.dtos.auth.LogoutRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.RefreshTokenRequest;

public interface SessionService {
    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
