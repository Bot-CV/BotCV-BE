package org.toanehihi.jobrecruitmentplatformserver.application.job.service;

import org.toanehihi.jobrecruitmentplatformserver.domain.model.Account;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.CreateJobRequest;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.JobResponse;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.PageResult;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.UpdateJobRequest;

public interface JobService {
    JobResponse findJobById(Long id);

    PageResult<JobResponse> getAllJobs(int page, int size, String sortBy, String sortDir);

    PageResult<JobResponse> getPublishJobs(int page, int size, String sortBy, String sortDir);

    JobResponse createJob(Account account, CreateJobRequest createJobRequest);

    JobResponse updateJob(Account account, Long id, UpdateJobRequest request);

    JobResponse cancelJob(Account account, Long id);

    JobResponse moderateJobPosting(Account account, Long id, String action);

    void deleteJob(Long id);

}
