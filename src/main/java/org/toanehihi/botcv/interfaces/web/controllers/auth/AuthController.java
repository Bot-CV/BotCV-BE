package org.toanehihi.botcv.interfaces.web.controllers.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.toanehihi.botcv.application.auth.service.AuthenticationService;
import org.toanehihi.botcv.application.auth.service.RegistrationService;
import org.toanehihi.botcv.application.auth.service.SessionService;
import org.toanehihi.botcv.interfaces.web.dtos.DataResponse;
import org.toanehihi.botcv.interfaces.web.dtos.account.AccountResponse;
import org.toanehihi.botcv.interfaces.web.dtos.account.CandidateAccountRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.RecruiterAccountRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.AuthenticationResponse;
import org.toanehihi.botcv.interfaces.web.dtos.auth.GoogleLoginRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.LoginRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.LogoutRequest;
import org.toanehihi.botcv.interfaces.web.dtos.auth.RefreshTokenRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final SessionService sessionService;

    @PostMapping("/register/candidate")
    public DataResponse<AccountResponse> candidateRegister(@Valid @RequestBody CandidateAccountRequest request) {
        return DataResponse.<AccountResponse>builder()
                .data(registrationService.candidateRegister(request))
                .build();
    }

    @PostMapping("/register/recruiter")
    public DataResponse<AccountResponse> recruiterRegister(@Valid @RequestBody RecruiterAccountRequest request) {
        return DataResponse.<AccountResponse>builder()
                .data(registrationService.recruiterRegister(request))
                .build();
    }

    @PostMapping("/login")
    public DataResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return DataResponse.<AuthenticationResponse>builder()
                .data(authenticationService.login(request))
                .build();
    }

    @PostMapping("/login/google")
    public DataResponse<AuthenticationResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        return DataResponse.<AuthenticationResponse>builder()
                .data(authenticationService.loginWithGoogle(request))
                .build();
    }

    @PostMapping("/refresh")
    public DataResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return DataResponse.<AuthenticationResponse>builder()
                .data(sessionService.refreshToken(request))
                .build();
    }

    @PostMapping("/logout")
    public DataResponse<String> logout(@RequestBody LogoutRequest request) {
        sessionService.logout(request);
        return DataResponse.<String>builder()
                .data("Logout successfully")
                .build();
    }
}
