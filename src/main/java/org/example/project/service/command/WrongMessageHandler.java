package org.example.project.service.command;

import lombok.extern.slf4j.Slf4j;
import org.example.project.service.TelegramMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class WrongMessageHandler implements CommandHandler{
    @Autowired
    private TelegramMessageSender telegramMessageSender;

    @Override
    public void handleCommand(long chatId, Update update) {
        log.info("Wrong command received from user {}", chatId);
        telegramMessageSender.sendMessage(chatId, "Sorry, command was not recognized");
    }
}
