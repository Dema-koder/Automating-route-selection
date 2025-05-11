package org.example.project.service.command.note;

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
public class NoteCommandHandlerFactory {
    private final List<NoteCommandHandler> noteHandlers;
    private final WrongMessageHandler wrongHandler;
    private boolean isPreviousAddNote = false;

    public CommandHandler getHandler(String command) {
        for (var handler: noteHandlers)
            if (handler.canHandle(command, isPreviousAddNote)) {
                isPreviousAddNote = handler instanceof AddNoteHandler;
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
