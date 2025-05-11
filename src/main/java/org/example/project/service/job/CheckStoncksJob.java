package org.example.project.service.job;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.configuration.InvestConfig;
import org.example.project.domain.Stoncks;
import org.example.project.repository.StoncksRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckStoncksJob {
    private final InvestConfig investConfig;
    private final StoncksRepository stoncksRepository;
    private InvestApi api;

    @PostConstruct
    public void init() {
        if (investConfig.getToken() != null) {
            this.api = InvestApi.create(investConfig.getToken());
        }
    }

    @Scheduled(fixedRate = 10000L)
    public void getStoncks() {
        if (api == null || investConfig.getToken() == null) {
            return;
        }

        try {
            Portfolio portfolio = api.getOperationsService()
                    .getPortfolioSync(investConfig.getUserId());

            BigDecimal value = portfolio.getTotalAmountPortfolio().getValue();

            stoncksRepository.save(Stoncks.builder()
                    .time(LocalDateTime.now())
                    .value(value)
                    .build());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }
}