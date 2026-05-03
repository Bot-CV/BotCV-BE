package org.toanehihi.botcv.application.company.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.toanehihi.botcv.application.email.service.EmailService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.*;
import org.toanehihi.botcv.infrastructure.persistence.mappers.company.CompanyMapper;
import org.toanehihi.botcv.infrastructure.persistence.repositories.CompanyRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.LocationRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.RecruiterRepository;
import org.toanehihi.botcv.infrastructure.security.CurrentAccountProvider;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.company.CompanyLocationRequest;
import org.toanehihi.botcv.interfaces.web.dtos.company.CompanyRequest;
import org.toanehihi.botcv.interfaces.web.dtos.company.CompanyResponse;
import org.toanehihi.botcv.interfaces.web.dtos.company.VerifyCompanyRequest;
import org.toanehihi.botcv.interfaces.web.dtos.company.VerifyCompanyResponse;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final EmailService emailService;
    private final CurrentAccountProvider currentAccountProvider;
    private final RecruiterRepository recruiterRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public VerifyCompanyResponse verifyAttestation(Account account, VerifyCompanyRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        company.setVerified(request.isApproved());
        Company savedCompany = companyRepository.save(company);

        company.getRecruiters().stream().findFirst().ifPresent(recruiter ->
                emailService.sendCompanyVerificationResult(
                        recruiter.getAccount().getEmail(),
                        request.isApproved(),
                        request.isApproved() ? null : request.getReason()));

        return VerifyCompanyResponse.builder()
                .companyId(savedCompany.getId())
                .approved(request.isApproved())
                .reason(request.getReason())
                .build();
    }

    @Override
    public PageResult<CompanyResponse> getVerifyList(Account account, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Company> unverifiedCompaniesPage = companyRepository.findByVerifiedFalse(pageable);
        return PageResult.from(unverifiedCompaniesPage.map(companyMapper::toResponse));
    }

    @Override
    public CompanyResponse getCompanyInfo(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public Company createCompany(String companyName) {
        Optional<Company> company = companyRepository.findByName(companyName);

        if (company.isPresent()) {
            return company.get();
        }
        company = Optional.of(companyRepository.save(Company.builder()
                .name(companyName)
                .build()));
        return company.get();
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(CompanyRequest request) {
        Account account = currentAccountProvider.getCurrentAccount();
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));

        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.RECRUITER_COMPANY_NOT_FOUND);
        }

        Set<CompanyLocation> updatedLocations = new HashSet<>();
        for (CompanyLocationRequest locationRequest : request.getCompanyLocations()) {
            Location location = Location.builder()
                    .streetAddress(locationRequest.getLocation().getStreetAddress())
                    .ward(locationRequest.getLocation().getWard())
                    .district(locationRequest.getLocation().getDistrict())
                    .provinceCity(locationRequest.getLocation().getProvinceCity())
                    .country(locationRequest.getLocation().getCountry())
                    .build();
            locationRepository.save(location);

            CompanyLocation companyLocation = CompanyLocation.builder()
                    .company(company)
                    .location(location)
                    .isHeadquarter(locationRequest.getIsHeadquarter())
                    .build();
            updatedLocations.add(companyLocation);
        }
        company.getCompanyLocations().clear();
        company.getCompanyLocations().addAll(updatedLocations);

        companyMapper.updateCompany(company, request);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponse(savedCompany);
    }
}
