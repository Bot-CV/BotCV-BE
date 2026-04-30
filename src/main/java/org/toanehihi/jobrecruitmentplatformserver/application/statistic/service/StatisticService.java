package org.toanehihi.jobrecruitmentplatformserver.application.statistic.service;

import org.toanehihi.jobrecruitmentplatformserver.domain.model.Account;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.statistic.AdminStatisticResponse;
import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.statistic.StatisticResponse;

public interface StatisticService {
    StatisticResponse getPlatformStatistics(Account account);

    AdminStatisticResponse getAdminStatistics(Account account);
}
