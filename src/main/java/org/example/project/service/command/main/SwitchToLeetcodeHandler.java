package org.example.project.service.command.main;

import lombok.RequiredArgsConstructor;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SwitchToLeetcodeHandler implements CommandHandler, MainCommandHandler {

    private final TelegramMessageSender messageSender;
    private final UserSessionService userSessionService;
    private final ReplyKeyboardMarkup leetcodeMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        messageSender.sendMessageWithKeyboard(
                chatId,
                "Here you can:\n1) Get random task from leetcode\n2) Get random with difficulty and tags",
                leetcodeMenuKeyboard
        );
        userSessionService.setDialogMode(chatId, DialogMode.LEETCODE);
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("/leetcode");
    }
}
