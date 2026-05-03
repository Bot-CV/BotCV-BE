package org.toanehihi.botcv.application.interview.service;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.toanehihi.botcv.application.email.service.EmailService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.domain.model.*;
import org.toanehihi.botcv.domain.model.enums.ApplicationStatus;
import org.toanehihi.botcv.infrastructure.persistence.mappers.interview.InterviewMapper;
import org.toanehihi.botcv.infrastructure.persistence.repositories.InterviewRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.JobApplicationRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.LocationRepository;
import org.toanehihi.botcv.infrastructure.persistence.repositories.RecruiterRepository;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.interview.CreateInterviewRequest;
import org.toanehihi.botcv.interfaces.web.dtos.interview.InterviewResponse;
import org.toanehihi.botcv.interfaces.web.dtos.interview.UpdateInterviewRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewServiceImpl implements InterviewService {
    private final RecruiterRepository recruiterRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewRepository interviewRepository;
    private final LocationRepository locationRepository;
    private final InterviewMapper interviewMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public InterviewResponse scheduleInterview(Account account, CreateInterviewRequest request) {
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        JobApplication jobApplication = jobApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_APPLICATION_NOT_FOUND));

        if (!jobApplication.getJob().getCompany().getId().equals(recruiter.getCompany().getId())) {
            throw new AppException(ErrorCode.RECRUITER_UNAUTHORIZED_ACCESS_JOB_APPLICANTS);
        }

        Interview interview = interviewRepository.save(interviewMapper.toEntity(request));

        jobApplication.setStatus(ApplicationStatus.INTERVIEW);
        jobApplicationRepository.save(jobApplication);

        emailService.sendInterviewInvitationEmail(
                interview.getLocation(),
                interview.getScheduledAt(),
                jobApplication.getCandidate().getFullName(),
                jobApplication.getCandidate().getAccount().getEmail());

        return interviewMapper.toResponse(interview);
    }

    @Override
    @Transactional
    public InterviewResponse updateInterview(Account account, UpdateInterviewRequest request) {
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_FOUND));

        OffsetDateTime oldScheduledAt = interview.getScheduledAt();

        if (!(interview.getJobApplication().getJob().getCompany().getId().equals(recruiter.getCompany().getId()))) {
            throw new AppException(ErrorCode.RECRUITER_UNAUTHORIZED_ACCESS_INTERVIEW);
        }

        interview.setScheduledAt(request.getScheduledAt());
        interview.setNotes(request.getNotes());
        interview.setStatus(request.getStatus());
        interview.setLocation(locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND)));

        interview = interviewRepository.save(interview);

        emailService.sendInterviewUpdateEmail(interview.getLocation(),
                oldScheduledAt,
                interview.getScheduledAt(),
                interview.getJobApplication().getCandidate().getFullName(),
                interview.getJobApplication().getCandidate().getAccount().getEmail());

        return interviewMapper.toResponse(interview);
    }

    @Override
    public PageResult<InterviewResponse> getAllInterviews(Account account, int page, int size, String sortBy,
            String sortDir) {
        Recruiter recruiter = recruiterRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_RECRUITER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy));

        Page<InterviewResponse> interviews = interviewRepository
                .findByJobApplication_Job_Company_Id(recruiter.getCompany().getId(), pageable)
                .map(interviewMapper::toResponse);

        return PageResult.from(interviews);
    }
}
