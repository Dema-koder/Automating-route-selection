package org.example.project.service.command.leetcode;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.command.WrongMessageHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeetcodeCommandHandlerFactory {
    private final List<LeetcodeCommandHandler> noteHandlers;
    private final WrongMessageHandler wrongHandler;
    private LeetcodeMode mode;
    @Setter
    @Getter
    private static String difficulty = "";
    @Setter
    @Getter
    private static String tag = "";

    public CommandHandler getHandler(String command) {
        for (var handler: noteHandlers)
            if (handler.canHandle(command, mode)) {
                if (handler instanceof RandomWithHandler)
                    mode = LeetcodeMode.DIFFICULTY;
                if (handler instanceof DifficultyHandler)
                    mode = LeetcodeMode.TAGS;
                if (handler instanceof GotTagHandler)
                    mode = LeetcodeMode.MAIN;
                return (CommandHandler) handler;
            }
        return wrongHandler;
    }

    @PostConstruct
    public void init() {
        log.info("Registered handlers: {}",
                noteHandlers.stream()
                        .map(h -> h.getClass().getSimpleName())
                        .collect(Collectors.joining(", ")));
    }
}
