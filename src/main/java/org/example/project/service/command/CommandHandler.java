package org.example.project.service.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    void handleCommand(long chatId, Update update);
}
