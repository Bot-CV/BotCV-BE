package org.toanehihi.botcv.application.candidate.service;

import org.springframework.web.multipart.MultipartFile;
import org.toanehihi.botcv.domain.model.Candidate;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.candidate.CandidateResumeResponse;
import org.toanehihi.botcv.interfaces.web.dtos.candidate.CandidateRequest;
import org.toanehihi.botcv.interfaces.web.dtos.candidate.CandidateResponse;
import org.toanehihi.botcv.interfaces.web.dtos.candidate.UserProfileBasedResponse;
import org.toanehihi.botcv.interfaces.web.dtos.job.SavedJobResponse;
import org.toanehihi.botcv.interfaces.web.dtos.job.application.JobApplicationResponse;
import org.toanehihi.botcv.interfaces.web.dtos.resource.ResumeAnalysisResponse;

public interface CandidateService {

    void save(Candidate candidate);

    CandidateResponse getProfile();

    CandidateResponse updateProfile(CandidateRequest request);

    SavedJobResponse saveJob(Long jobId);

    void removeSavedJob(Long jobId);

    JobApplicationResponse applyJob(Long jobId, MultipartFile cv);

    PageResult<JobApplicationResponse> getAllApplications(int page, int size, String sortBy, String sortDir);

    PageResult<SavedJobResponse> getAllSavedJobs(int page, int size, String sortBy, String sortDir);

    PageResult<CandidateResumeResponse> getCandidateResumes(int page, int size, String sortBy, String sortDir);

    UserProfileBasedResponse getUserProfileBasedData(Long candidateId);

    void updateProfileFromCV(Long accountId, ResumeAnalysisResponse cvData);
}
