package org.toanehihi.botcv.application.resource.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.domain.model.Resource;
import org.toanehihi.botcv.domain.model.enums.ResourceType;
import org.toanehihi.botcv.interfaces.web.dtos.resource.ResourceResponse;
import org.toanehihi.botcv.interfaces.web.dtos.resource.ResumeAnalysisResponse;

import java.util.List;
import java.util.Optional;

public interface ResourceService {
    Optional<Resource> getResource(Long id);

    Optional<Resource> findByPublicId(String publicId);

    ResourceResponse updateUserAvatar(Account account, MultipartFile avatar);

    ResourceResponse updateCompanyLogo(Account account, MultipartFile logo);

    ResourceResponse uploadResume(Account account, MultipartFile file);

    ResumeAnalysisResponse analyzeResume(Long resourceId);
}
