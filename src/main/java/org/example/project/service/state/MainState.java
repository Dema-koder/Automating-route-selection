package org.example.project.service.state;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.command.main.MainCommandHandlerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainState implements DialogState{

    private final MainCommandHandlerFactory commandHandlerFactory;

    @Override
    public void handleUpdate(Update update) {
        String command = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        CommandHandler handler = commandHandlerFactory.getHandler(command);
        handler.handleCommand(chatId, update);
    }
}
