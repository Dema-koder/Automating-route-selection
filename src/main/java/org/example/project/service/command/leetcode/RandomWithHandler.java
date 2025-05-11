package org.example.project.service.command.leetcode;

import lombok.RequiredArgsConstructor;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class RandomWithHandler implements CommandHandler, LeetcodeCommandHandler {

    private final TelegramMessageSender messageSender;
    private final ReplyKeyboardMarkup difficultyMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        messageSender.sendMessageWithKeyboard(chatId, "Choose difficulty", difficultyMenuKeyboard);
    }

    @Override
    public boolean canHandle(String command, LeetcodeMode mode) {
        return command.equals("/randomtaskwith");
    }
}
