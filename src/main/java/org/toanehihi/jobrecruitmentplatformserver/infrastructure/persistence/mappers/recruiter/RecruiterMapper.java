package org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.mappers.recruiter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.toanehihi.jobrecruitmentplatformserver.domain.exception.AppException;
import org.toanehihi.jobrecruitmentplatformserver.domain.exception.ErrorCode;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Recruiter;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.enums.ResourceType;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.mappers.company.CompanyMapper;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.mappers.resource.ResourceMapper;
import org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.repositories.ResourceRepository;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.recruiter.RecruiterResponse;

@Component
@RequiredArgsConstructor
public class RecruiterMapper {
    private final CompanyMapper companyMapper;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public RecruiterResponse toResponse(Recruiter recruiter) {
        return RecruiterResponse.builder()
                .id(recruiter.getId())
                .accountId(recruiter.getAccount().getId())
                .fullName(recruiter.getFullName())
                .phone(recruiter.getPhone())
                .resource(resourceMapper.toResponse(resourceRepository.findByIdAndResourceType(
                        recruiter.getAvatarResourceId(),
                        ResourceType.AVATAR)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
                ))
                .email(recruiter.getAccount().getEmail())
                .company(companyMapper.toResponse(recruiter.getCompany()))
                .dateCreated(recruiter.getDateCreated())
                .dateUpdated(recruiter.getDateUpdated())
                .build();
    }
}
