package org.toanehihi.botcv.application.recruiter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.*;
import org.toanehihi.botcv.domain.model.enums.ApplicationStatus;
import org.toanehihi.botcv.domain.model.enums.JobStatus;
import org.toanehihi.botcv.infrastructure.persistence.mappers.job.JobApplicationMapper;
import org.toanehihi.botcv.infrastructure.persistence.mappers.job.JobMapper;
import org.toanehihi.botcv.infrastructure.persistence.mappers.recruiter.RecruiterMapper;
import org.toanehihi.botcv.infrastructure.persistence.repositories.*;
import org.toanehihi.botcv.infrastructure.security.CurrentAccountProvider;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.job.JobResponse;
import org.toanehihi.botcv.interfaces.web.dtos.job.application.JobApplicantResponse;
import org.toanehihi.botcv.interfaces.web.dtos.recruiter.RecruiterRequest;
import org.toanehihi.botcv.interfaces.web.dtos.recruiter.RecruiterResponse;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruiterServiceImpl implements RecruiterService {

    private final CurrentAccountProvider currentAccountProvider;
    private final RecruiterRepository recruiterRepository;
    private final RecruiterMapper recruiterMapper;
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationMapper jobApplicationMapper;

    @Override
    @Transactional
    public Recruiter createRecruiter(Recruiter recruiter) {
        return recruiterRepository.save(recruiter);
    }

    @Override
    public RecruiterResponse getProfile() {
        return recruiterMapper.toResponse(getCurrentRecruiter());
    }

    @Override
    @Transactional
    public RecruiterResponse updateProfile(RecruiterRequest request) {
        Recruiter recruiter = getCurrentRecruiter();

        recruiter.setFullName(request.getFullName());
        Recruiter savedRecruiter = recruiterRepository.save(recruiter);
        return recruiterMapper.toResponse(savedRecruiter);
    }

    @Override
    public PageResult<JobResponse> getCompanyJobs(Account account, String jobStatus, int page, int size, String sortBy,
            String sortDir) {
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        if (recruiter.getCompany() == null) {
            throw new AppException(ErrorCode.RECRUITER_COMPANY_NOT_FOUND);
        }

        Sort.Direction direction = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Job> jobs = jobRepository.findJobsByCompany_IdAndStatus(recruiter.getCompany().getId(),
                JobStatus.valueOf(jobStatus), pageable);

        return PageResult.from(jobs.map(jobMapper::toResponse));
    }

    @Override
    public PageResult<JobApplicantResponse> getJobApplicants(Account account, Long jobId, int page, int size, String sortBy,
            String sortDir) {
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        if (recruiter.getCompany() == null) {
            throw new AppException(ErrorCode.RECRUITER_COMPANY_NOT_FOUND);
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getCompany().getId().equals(recruiter.getCompany().getId())) {
            throw new AppException(ErrorCode.RECRUITER_UNAUTHORIZED_ACCESS_JOB_APPLICANTS);
        }

        Sort.Direction direction = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<JobApplication> applications = jobApplicationRepository.findByJobId(jobId, pageable);

        return PageResult.from(applications.map(jobApplicationMapper::toApplicantResponse));
    }

    @Override
    @Transactional
    public JobApplicantResponse processCandidate(Account account, Long jobApplicationId, ApplicationStatus action) {
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_APPLICATION_NOT_FOUND));

        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        if (!jobApplication.getJob().getCompany().getId().equals(recruiter.getCompany().getId())) {
            throw new AppException(ErrorCode.RECRUITER_UNAUTHORIZED_ACCESS_JOB_APPLICANTS);
        }

        if (jobApplication.getStatus().equals(ApplicationStatus.REJECTED)) {
            throw new AppException(ErrorCode.JOB_ALREADY_PROCESSED);
        }

        jobApplication.setStatus(action);

        return jobApplicationMapper.toApplicantResponse(jobApplicationRepository.save(jobApplication));
    }

    // Private methods
    private Recruiter getCurrentRecruiter() {
        Account account = currentAccountProvider.getCurrentAccount();
        return recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_UNAUTHORIZED));
    }

}
