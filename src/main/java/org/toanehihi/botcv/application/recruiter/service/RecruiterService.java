package org.toanehihi.botcv.application.recruiter.service;

import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.domain.model.Recruiter;
import org.toanehihi.botcv.domain.model.enums.ApplicationStatus;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.job.JobResponse;
import org.toanehihi.botcv.interfaces.web.dtos.job.application.JobApplicantResponse;
import org.toanehihi.botcv.interfaces.web.dtos.recruiter.RecruiterRequest;
import org.toanehihi.botcv.interfaces.web.dtos.recruiter.RecruiterResponse;

public interface RecruiterService {
    Recruiter createRecruiter(Recruiter recruiter);

    RecruiterResponse getProfile();

    RecruiterResponse updateProfile(RecruiterRequest request);

    PageResult<JobResponse> getCompanyJobs(Account account, String jobStatus, int page, int size, String sortBy,
            String sortDir);

    PageResult<JobApplicantResponse> getJobApplicants(Account account, Long jobId, int page, int size, String sortBy,
            String sortDir);

    JobApplicantResponse processCandidate(Account account, Long jobApplicationId, ApplicationStatus action);
}
