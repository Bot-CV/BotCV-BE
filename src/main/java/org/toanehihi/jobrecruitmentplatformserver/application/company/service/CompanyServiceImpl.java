package org.toanehihi.jobrecruitmentplatformserver.application.company.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.toanehihi.jobrecruitmentplatformserver.application.cloud.service.CloudStorageService;
import org.toanehihi.jobrecruitmentplatformserver.application.cloud.service.CloudinaryStorageImpl.CloudinaryFileInfo;
import org.toanehihi.jobrecruitmentplatformserver.application.email.service.EmailService;
import org.toanehihi.jobrecruitmentplatformserver.domain.exception.AppException;
import org.toanehihi.jobrecruitmentplatformserver.domain.exception.ErrorCode;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Account;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.AttestationResource;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Company;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Recruiter;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Resource;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.enums.ResourceType;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.mappers.company.CompanyMapper;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.mappers.resource.ResourceMapper;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.repositories.AttestationResourceRepository;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.repositories.CompanyRepository;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.repositories.RecruiterRepository;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.repositories.ResourceRepository;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.annotation.HasAdminRole;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.annotation.HasRecruiterRole;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.PageResult;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.company.CompanyResponse;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.company.VerifyCompanyRequest;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.company.VerifyCompanyResponse;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.resource.ResourceResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final RecruiterRepository recruiterRepository;
    private final ResourceRepository resourceRepository;
    private final CompanyRepository companyRepository;
    private final AttestationResourceRepository attestationResourceRepository;
    private final ResourceMapper resourceMapper;
    private final CompanyMapper companyMapper;
    private final CloudStorageService cloudStorageService;
    private final EmailService emailService;

    private static final String ADMIN = "ADMIN";
    private static final String RECRUITER = "RECRUITER";

    @Override
    @HasRecruiterRole
    @Transactional
    public List<ResourceResponse> uploadAttestation(Account account, List<MultipartFile> files) {
        if (!account.getRole().getName().equals(RECRUITER)) {
            throw new AppException(ErrorCode.ACCESS_FORBIDDEN);
        }

        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        if (recruiter.getCompany().isVerified()) {
            throw new AppException(ErrorCode.COMPANY_HAS_BEEN_VERIFIED);
        }
        Company company = recruiter.getCompany();
        Set<AttestationResource> attestations = new HashSet<>();
        for (MultipartFile file : files) {
            CloudinaryFileInfo fileInfo = cloudStorageService.storeFile(file, "attestation");
            Resource resource = Resource.builder()
                    .mimeType(fileInfo.mimeType())
                    .resourceType(ResourceType.ATTESTATION)
                    .url(fileInfo.url())
                    .publicId(fileInfo.publicId())
                    .name(fileInfo.fileName())
                    .build();
            Resource savedResource = resourceRepository.save(resource);
            AttestationResource attestation = AttestationResource.builder()
                    .company(company)
                    .resource(savedResource)
                    .build();
            attestations.add(attestation);
        }
        company.setAttestations(attestations);
        companyRepository.save(company);
        return attestations.stream()
                .map(attestation -> resourceMapper.toResponse(attestation.getResource()))
                .toList();
    }

    @Override
    @HasAdminRole
    @Transactional
    public VerifyCompanyResponse verifyAttestation(Account account, VerifyCompanyRequest request) {
        if (!account.getRole().getName().equals(ADMIN)) {
            throw new AppException(ErrorCode.ACCESS_FORBIDDEN);
        }
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        if (!request.isApproved()) {
            List<AttestationResource> attesations = attestationResourceRepository.findByCompany(company);
            for (AttestationResource attestation : attesations) {
                Resource resource = attestation.getResource();
                cloudStorageService.deleteFile(resource.getPublicId());
                resourceRepository.delete(resource);
            }
            emailService.sendCompanyVerificationResult(company.getRecruiter().getAccount().getEmail(),
                    request.isApproved(), null);
        } else {
            emailService.sendCompanyVerificationResult(company.getRecruiter().getAccount().getEmail(),
                    request.isApproved(), request.getReason());
        }
        company.setVerified(request.isApproved());
        Company savedCompany = companyRepository.save(company);
        return VerifyCompanyResponse.builder()
                .companyId(savedCompany.getId())
                .isApproved(request.isApproved())
                .reason(request.getReason())
                .build();
    }

    @Override
    @HasAdminRole
    public PageResult<CompanyResponse> getVerifyList(Account account, int page, int size, String sortBy,
            String sortDir) {
        if (!account.getRole().getName().equals(ADMIN)) {
            throw new AppException(ErrorCode.ACCESS_FORBIDDEN);
        }
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Company> unverifiedCompaniesPage = companyRepository.findAllUnverifiedCompanies(pageable);
        return PageResult.from(unverifiedCompaniesPage.map(companyMapper::toResponse));

    }

    @Override
    public CompanyResponse getCompanyInfo(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        return companyMapper.toResponse(company);
    }

    @Override
    @HasAdminRole
    public List<ResourceResponse> getCompanyAttestations(Account account, Long companyId) {
        if (!account.getRole().getName().equals(ADMIN)) {
            throw new AppException(ErrorCode.ACCESS_FORBIDDEN);
        }
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        List<AttestationResource> attestations = attestationResourceRepository.findByCompany(company);
        return attestations.stream()
                .map(ar -> resourceMapper.toResponse(ar.getResource()))
                .toList();
    }

}
