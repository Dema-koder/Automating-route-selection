package org.example.project.service.command.invest;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.command.WrongMessageHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestCommandHandlerFactory {
    private final List<InvestCommandHandler> handlers;
    private final WrongMessageHandler wrongHandler;

    public CommandHandler getHandler(String command) {
        for (var handler: handlers)
            if (handler.canHandle(command))
                return (CommandHandler) handler;
        return wrongHandler;
    }

    @PostConstruct
    public void init() {
        log.info("Registered handlers: {}",
                handlers.stream()
                        .map(h -> h.getClass().getSimpleName())
                        .collect(Collectors.joining(", ")));
    }
}
