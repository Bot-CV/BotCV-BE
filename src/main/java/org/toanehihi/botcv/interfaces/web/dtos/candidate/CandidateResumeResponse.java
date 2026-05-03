package org.toanehihi.botcv.interfaces.web.dtos.candidate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.toanehihi.botcv.interfaces.web.dtos.resource.ResourceResponse;

import java.time.OffsetDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateResumeResponse {
    private Long id;
    private String title;
    private ResourceResponse resource;
    private OffsetDateTime dateCreated;
}
