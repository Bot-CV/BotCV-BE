package org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job;

import lombok.Builder;
import lombok.Getter;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.enums.JobStatus;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.enums.SeniorityLevel;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.enums.WorkMode;

import java.time.OffsetDateTime;

@Builder
@Getter
public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String jobRole;
    private SeniorityLevel seniority;
    private int minExperienceYears;
    private String location;
    private WorkMode workMode;
    private Integer salaryMin;
    private Integer salaryMax;
    private String currency;
    private Integer maxCandidates;
    private OffsetDateTime datePosted;
    private OffsetDateTime dateExpires;
    private JobStatus status;
    private String summary;
    private String responsibilities;
    private String requirements;
    private String niceToHave;
    private String benefits;
    private String hiringProcess;
    private String notes;
}
