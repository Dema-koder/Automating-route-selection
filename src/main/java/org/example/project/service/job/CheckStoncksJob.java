package org.example.project.service.job;

import lombok.RequiredArgsConstructor;
import org.example.project.configuration.InvestConfig;
import org.example.project.domain.Stoncks;
import org.example.project.repository.StoncksRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CheckStoncksJob {

    private final InvestConfig investConfig;
    private final StoncksRepository stoncksRepository;

    @Scheduled(fixedRate = 10000L)
    private void getStoncks() {
        if (investConfig.getToken() == null)
            return;
        var api = InvestApi.create(investConfig.getToken());
        Portfolio portfolio = api.getOperationsService().getPortfolioSync(investConfig.getUserId());
        BigDecimal value = portfolio.getTotalAmountPortfolio().getValue();
        stoncksRepository.save(Stoncks.builder()
                .time(LocalDateTime.now())
                .value(value)
                .build());
    }
}
