package org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.statistic;

import lombok.Builder;
import lombok.Data;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.job.JobResponse;

import java.util.List;
import java.util.TreeMap;

@Builder
@Data
public class StatisticResponse {
    Long currentPublishJobCount;

    Long totalNewApplicationCount;

    Long totalPendingApplicationCount;

    TreeMap<Integer, Long> weeklyApplicationCount;

    List<JobResponse> newestJobs;

    List<NewestJobApplication> newestJobApplications;
}
