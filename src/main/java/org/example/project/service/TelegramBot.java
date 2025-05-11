package org.example.project.service;

import lombok.extern.slf4j.Slf4j;
import org.example.project.configuration.ApplicationConfig;
import org.example.project.service.state.DialogStateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot{

    final ApplicationConfig config;

    private final ExecutorService executorService;

    @Autowired
    private UserSessionService userSessionService;

    @Lazy
    @Autowired
    private DialogStateFactory dialogStateFactory;

    @Autowired
    public TelegramBot(ApplicationConfig config) {
        this.config = config;
        this.executorService = Executors.newFixedThreadPool(10);
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "register"));
        listOfCommand.add(new BotCommand("/unregister", "delete my data"));
        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Get update from {}", update.getMessage().getChatId());
        executorService.submit(() -> {
            var mode = userSessionService.getSession(update.getMessage().getChatId()).getDialogMode();
            var state = dialogStateFactory.getState(mode);
            state.handleUpdate(update);
        });
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
