package org.toanehihi.botcv.application.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.toanehihi.botcv.application.account.service.AccountService;
import org.toanehihi.botcv.application.candidate.service.CandidateService;
import org.toanehihi.botcv.application.company.service.CompanyService;
import org.toanehihi.botcv.application.recruiter.service.RecruiterService;
import org.toanehihi.botcv.application.resource.service.ResourceService;
import org.toanehihi.botcv.application.role.service.RoleService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.*;
import org.toanehihi.botcv.domain.model.enums.RoleName;
import org.toanehihi.botcv.infrastructure.persistence.mappers.account.AccountMapper;
import org.toanehihi.botcv.interfaces.web.dtos.account.AccountResponse;
import org.toanehihi.botcv.interfaces.web.dtos.account.CandidateAccountRequest;
import org.toanehihi.botcv.interfaces.web.dtos.account.RecruiterAccountRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final RoleService roleService;
    private final CandidateService candidateService;
    private final RecruiterService recruiterService;
    private final CompanyService companyService;
    private final ResourceService resourceService;

    @Value("${app.default-avatar-public-id}")
    private String defaultAvatarPublicId;

    @Override
    @Transactional
    public AccountResponse candidateRegister(CandidateAccountRequest request) {
        if (accountService.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        Role role = roleService.findByName(RoleName.CANDIDATE.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        Account newAccount = accountMapper.toEntity(request.getEmail(), request.getPassword());
        newAccount.setRole(role);
        Account savedAccount = accountService.save(newAccount);

        candidateService.createCandidate(Candidate.builder()
                .account(savedAccount)
                .fullName(request.getFullName())
                .avatar(getDefaultAvatar())
                .build());

        return accountMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponse recruiterRegister(RecruiterAccountRequest request) {
        if (accountService.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        Role role = roleService.findByName(RoleName.RECRUITER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        Account account = accountMapper.toEntity(request.getEmail(), request.getPassword());
        account.setRole(role);
        Account savedAccount = accountService.save(account);

        Company company = companyService.createCompany(request.getCompanyName());

        recruiterService.createRecruiter(Recruiter.builder()
                .account(savedAccount)
                .fullName(request.getFullName())
                .avatar(getDefaultAvatar())
                .company(company)
                .build());

        return accountMapper.toResponse(savedAccount);
    }

    private Resource getDefaultAvatar() {
        return resourceService.findByPublicId(defaultAvatarPublicId).orElse(null);
    }
}
