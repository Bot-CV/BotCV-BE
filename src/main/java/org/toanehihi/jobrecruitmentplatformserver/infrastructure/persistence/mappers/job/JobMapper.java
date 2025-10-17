package org.toanehihi.jobrecruitmentplatformserver.infrastructure.persistence.mappers.job;

import org.springframework.stereotype.Component;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.JobDescription;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.CreateJobRequest;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.JobDetailResponse;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.JobResponse;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Job;
import org.toanehihi.jobrecruitmentplatformserver.domain.model.Location;

@Component
public class JobMapper {
    public JobResponse toResponse(Job job){
        Location jobLocation = job.getLocation();
        String location = null;
        if (jobLocation != null){
            location = jobLocation.getStreetAddress() + ", " +
                    jobLocation.getWard() + ", " +
                    jobLocation.getDistrict() + ", " +
                    jobLocation.getProvinceCity() + ", " +
                    jobLocation.getCountry();
        }

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany().getName())
                .jobRole(job.getJobRole().getName())
                .seniority(job.getSeniority())
                .minExperienceYears(job.getMinExperienceYears())
                .location(location)
                .workMode(job.getWorkMode())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .datePosted(job.getDatePosted())
                .dateExpires(job.getDateExpires())
                .status(job.getStatus())
                .maxCandidates(job.getMaxCandidates() != null ? job.getMaxCandidates() : null)
                .build();
    }

    public JobDetailResponse toJobDetailResponse(Job job){
        Location jobLocation = job.getLocation();
        String location = null;
        if (jobLocation != null){
            location = jobLocation.getStreetAddress() + ", " +
                    jobLocation.getWard() + ", " +
                    jobLocation.getDistrict() + ", " +
                    jobLocation.getProvinceCity() + ", " +
                    jobLocation.getCountry();
        }

        return JobDetailResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany().getName())
                .jobRole(job.getJobRole().getName())
                .seniority(job.getSeniority())
                .minExperienceYears(job.getMinExperienceYears())
                .location(location)
                .workMode(job.getWorkMode())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .datePosted(job.getDatePosted())
                .dateExpires(job.getDateExpires())
                .status(job.getStatus())
                .maxCandidates(job.getMaxCandidates() != null ? job.getMaxCandidates() : null)
                .responsibilities(job.getDescription().getResponsibilities())
                .requirements(job.getDescription().getRequirements())
                .niceToHave(job.getDescription().getNiceToHave())
                .benefits(job.getDescription().getBenefits())
                .hiringProcess(job.getDescription().getHiringProcess())
                .notes(job.getDescription().getNotes())
                .summary(job.getDescription().getSummary())
                .build();
    }

    public Job toEntity(CreateJobRequest request){
        return Job.builder()
                .title(request.getTitle())
                .jobRole(null)
                .seniority(request.getSeniorityLevel())
                .employmentType(request.getEmploymentType())
                .minExperienceYears(request.getMinExperienceYears())
                .location(null)
                .workMode(request.getWorkMode())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .currency(request.getCurrency())
                .maxCandidates(request.getMaxCandidates() != null ? request.getMaxCandidates() : null)
                .dateExpires(request.getDateExpires())
                .description(JobDescription.builder()
                        .summary(request.getSummary())
                        .responsibilities(request.getResponsibilities())
                        .requirements(request.getRequirements())
                        .niceToHave(request.getNiceToHave())
                        .benefits(request.getBenefits())
                        .hiringProcess(request.getHiringProcess())
                        .notes(request.getNotes())
                        .build())
                .build();
    }
}
