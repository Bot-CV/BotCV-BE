package org.toanehihi.botcv.application.auth.service;

import org.toanehihi.botcv.interfaces.web.dtos.auth.AuthenticationResponse;
import org.toanehihi.botcv.interfaces.web.dtos.auth.GoogleLoginRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.LoginRequest;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest request);

    AuthenticationResponse loginWithGoogle(GoogleLoginRequest request);
}
