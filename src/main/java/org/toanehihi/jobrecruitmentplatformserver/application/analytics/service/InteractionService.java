package org.toanehihi.jobrecruitmentplatformserver.application.analytics.service;

import org.toanehihi.jobrecruitmentplatformserver.interfaces.web.dtos.interaction.InteractionEvent;

import java.util.List;

public interface InteractionService {
    void trackQuery(List<InteractionEvent> request);
}
