package org.toanehihi.botcv.infrastructure.persistence.mappers.recruiter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.toanehihi.botcv.domain.model.Recruiter;
import org.toanehihi.botcv.infrastructure.persistence.mappers.company.CompanyMapper;
import org.toanehihi.botcv.infrastructure.persistence.mappers.resource.ResourceMapper;
import org.toanehihi.botcv.interfaces.web.dtos.recruiter.RecruiterResponse;

@Component
@RequiredArgsConstructor
public class RecruiterMapper {
    private final CompanyMapper companyMapper;
    private final ResourceMapper resourceMapper;

    public RecruiterResponse toResponse(Recruiter recruiter) {
        return RecruiterResponse.builder()
                .id(recruiter.getId())
                .accountId(recruiter.getAccount().getId())
                .fullName(recruiter.getFullName())
                .phone(recruiter.getPhone())
                .resource(recruiter.getAvatar() != null
                        ? resourceMapper.toResponse(recruiter.getAvatar())
                        : null)
                .email(recruiter.getAccount().getEmail())
                .company(companyMapper.toResponse(recruiter.getCompany()))
                .dateCreated(recruiter.getDateCreated())
                .dateUpdated(recruiter.getDateUpdated())
                .build();
    }
}
