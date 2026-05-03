package org.toanehihi.botcv.infrastructure.persistence.mappers.job;

import org.springframework.stereotype.Component;
import org.toanehihi.botcv.domain.model.JobCategory;
import org.toanehihi.botcv.interfaces.web.dtos.job.category.JobCategoryResponse;

@Component
public class JobCategoryMapper {

    public JobCategoryResponse toResponse(JobCategory category) {
        return JobCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .leaf(category.isLeaf())
                .build();
    }
}
