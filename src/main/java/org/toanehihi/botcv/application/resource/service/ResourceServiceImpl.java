package org.toanehihi.botcv.application.resource.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.toanehihi.botcv.application.candidate.service.CandidateService;
import org.toanehihi.botcv.application.cloud.service.CloudStorageService;
import org.toanehihi.botcv.application.cloud.service.CloudinaryStorageImpl;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.*;
import org.toanehihi.botcv.domain.model.enums.ResourceType;
import org.toanehihi.botcv.domain.model.enums.RoleName;
import org.toanehihi.botcv.infrastructure.persistence.mappers.resource.ResourceMapper;
import org.toanehihi.botcv.infrastructure.persistence.repositories.CandidateRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.CompanyRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.JobApplicationRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.RecruiterRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.ResourceRepository;
import org.toanehihi.botcv.interfaces.web.dtos.resource.FileData;
import org.toanehihi.botcv.interfaces.web.dtos.resource.ResourceResponse;
import org.toanehihi.botcv.interfaces.web.dtos.resource.ResumeAnalysisResponse;

import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceServiceImpl implements ResourceService {
    private final RecruiterRepository recruiterRepository;
    private final CandidateRepository candidateRepository;
    private final ResourceRepository resourceRepository;
    private final CompanyRepository companyRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ResourceMapper resourceMapper;
    private final CloudStorageService cloudStorageService;
    private final CandidateService candidateService;

    private final RestTemplate restTemplate;

    @Value("${app.ner-service-url}")
    private String nerServiceUrl;

    @Value("${app.cloudinary.base-url:}")
    private String cloudinaryBaseUrl;

    @Override
    public Optional<Resource> getResource(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public Optional<Resource> findByPublicId(String publicId) {
        return resourceRepository.findByPublicId(publicId);
    }

    @Override
    @Transactional
    public ResourceResponse updateUserAvatar(Account account, MultipartFile avatar) {
        RoleName role = RoleName.valueOf(account.getRole().getName());

        return switch (role) {
            case RECRUITER -> {
                Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));
                yield updateAvatar(
                        recruiter.getAvatar(),
                        avatar,
                        resource -> {
                            recruiter.setAvatar(resource);
                            recruiter.setDateUpdated(OffsetDateTime.now());
                            recruiterRepository.save(recruiter);
                        });
            }
            case CANDIDATE -> {
                Candidate candidate = candidateRepository.findByAccountId(account.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_CANDIDATE_NOT_FOUND));
                yield updateAvatar(
                        candidate.getAvatar(),
                        avatar,
                        resource -> {
                            candidate.setAvatar(resource);
                            candidate.setDateUpdated(OffsetDateTime.now());
                            candidateRepository.save(candidate);
                        });
            }
            default -> throw new AppException(ErrorCode.ACCOUNT_DOES_NOT_SUPPORT);
        };
    }

    private ResourceResponse updateAvatar(
            Resource currentAvatar,
            MultipartFile avatar,
            Consumer<Resource> updateEntity) {

        if (currentAvatar != null) {
            resourceRepository.delete(currentAvatar);
            cloudStorageService.deleteFile(currentAvatar.getPublicId());
        }

        // Upload and save new avatar
        CloudinaryStorageImpl.CloudinaryFileInfo fileInfo = cloudStorageService.storeFile(avatar, "avatar");

        Resource resource = Resource.builder()
                .contentType(fileInfo.contentType())
                .resourceType(ResourceType.IMAGE)
                .publicId(fileInfo.publicId())
                .size(fileInfo.size())
                .name(fileInfo.fileName())
                .build();

        Resource savedResource = resourceRepository.save(resource);

        updateEntity.accept(savedResource);

        return resourceMapper.toResponse(savedResource);
    }

    @Override
    @Transactional
    public ResourceResponse updateCompanyLogo(Account account, MultipartFile logo) {
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.RECRUITER_COMPANY_NOT_FOUND);
        }

        Resource currentLogo = company.getLogo();
        if (currentLogo != null) {
            resourceRepository.delete(currentLogo);
            cloudStorageService.deleteFile(currentLogo.getPublicId());
        }
        CloudinaryStorageImpl.CloudinaryFileInfo fileInfo = cloudStorageService.storeFile(logo, "company_logo");
        Resource resource = Resource.builder()
                .contentType(fileInfo.contentType())
                .resourceType(ResourceType.IMAGE)
                .publicId(fileInfo.publicId())
                .size(fileInfo.size())
                .name(fileInfo.fileName())
                .build();
        Resource savedResource = resourceRepository.save(resource);
        company.setLogo(savedResource);
        companyRepository.save(company);
        return resourceMapper.toResponse(savedResource);
    }

    @Override
    @Transactional
    public ResourceResponse uploadResume(Account account, MultipartFile file) {
        candidateRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_CANDIDATE_NOT_FOUND));
        CloudinaryStorageImpl.CloudinaryFileInfo fileInfo = cloudStorageService.storeFile(file, "resume");
        Resource resource = Resource.builder()
                .contentType(fileInfo.contentType())
                .resourceType(ResourceType.DOCUMENT)
                .publicId(fileInfo.publicId())
                .size(fileInfo.size())
                .name(fileInfo.fileName())
                .build();
        Resource savedResource = resourceRepository.save(resource);
        return resourceMapper.toResponse(savedResource);
    }

    @Override
    @Transactional
    public ResumeAnalysisResponse analyzeResume(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        if (resource.getResourceType() != ResourceType.DOCUMENT) {
            throw new AppException(ErrorCode.RESOURCE_TYPE_NOT_ALLOWED);
        }
        String resourceUrl = buildResourceUrl(resource.getPublicId());
        FileData fileData = cloudStorageService.downloadFile(resourceUrl);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(fileData.getContent()) {
            @Override
            public String getFilename() {
                return resource.getName();
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<NerExtractResponse> response = restTemplate.postForEntity(
                nerServiceUrl + "/extract",
                requestEntity,
                NerExtractResponse.class);

        NerExtractResponse wrapper = response.getBody();
        ResumeAnalysisResponse analysisResult = (wrapper != null) ? wrapper.entities : null;

        if (analysisResult != null) {
            jobApplicationRepository.findFirstByCvResourceId(resourceId)
                    .map(JobApplication::getCandidate)
                    .ifPresent(candidate ->
                            candidateService.updateProfileFromCV(candidate.getAccount().getId(), analysisResult));
        }
        return analysisResult;
    }

    private String buildResourceUrl(String publicId) {
        if (publicId == null) return null;
        if (cloudinaryBaseUrl != null && !cloudinaryBaseUrl.isBlank()) {
            return cloudinaryBaseUrl + "/" + publicId;
        }
        return publicId;
    }

    public record NerExtractResponse(ResumeAnalysisResponse entities) {
    }
}
