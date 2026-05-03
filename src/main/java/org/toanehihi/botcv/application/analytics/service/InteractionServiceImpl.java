package org.toanehihi.botcv.application.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.toanehihi.botcv.application.account.service.AccountService;
import org.toanehihi.botcv.domain.exception.AppException;
import org.toanehihi.botcv.domain.exception.ErrorCode;
import org.toanehihi.botcv.infrastructure.security.CurrentAccountProvider;
import org.toanehihi.botcv.interfaces.web.dtos.interaction.InteractionEvent;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteractionServiceImpl implements InteractionService {
    private final AccountService accountService;
    private final CurrentAccountProvider currentAccountProvider;
    private final InteractionEventPublisher interactionEventPublisher;

    @Override
    public void trackQuery(List<InteractionEvent> request) {
        Long accountId = currentAccountProvider.getCurrentAccountId();

        if (!accountService.existsById(accountId)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        List<InteractionEvent> events = request.stream()
                .peek(e -> e.setAccountId(accountId))
                .toList();

        // Publish interaction events to Redis Stream for async processing
        interactionEventPublisher.publish(events);
    }
}
