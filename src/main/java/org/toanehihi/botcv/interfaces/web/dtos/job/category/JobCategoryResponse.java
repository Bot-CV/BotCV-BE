package org.toanehihi.botcv.interfaces.web.dtos.job.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobCategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private boolean leaf;
    private List<JobCategoryResponse> children;
}
