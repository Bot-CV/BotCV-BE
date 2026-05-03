package org.toanehihi.botcv.application.interview.service;

import org.toanehihi.botcv.domain.model.Account;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.interview.CreateInterviewRequest;
import org.toanehihi.botcv.interfaces.web.dtos.interview.InterviewResponse;
import org.toanehihi.botcv.interfaces.web.dtos.interview.UpdateInterviewRequest;

public interface InterviewService {
    InterviewResponse scheduleInterview(Account account, CreateInterviewRequest request);

    InterviewResponse updateInterview(Account account, UpdateInterviewRequest request);

    PageResult<InterviewResponse> getAllInterviews(Account account, int page, int size, String sortBy, String sortDir);
}
