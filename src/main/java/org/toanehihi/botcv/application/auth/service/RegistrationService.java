package org.toanehihi.botcv.application.auth.service;

import org.toanehihi.botcv.interfaces.web.dtos.account.AccountResponse;
import org.toanehihi.botcv.interfaces.web.dtos.account.CandidateAccountRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.RecruiterAccountRequest;

public interface RegistrationService {
    AccountResponse candidateRegister(CandidateAccountRequest request);

    AccountResponse recruiterRegister(RecruiterAccountRequest request);
}
