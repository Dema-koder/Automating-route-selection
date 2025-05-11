package org.example.project.service.command.leetcode;

import lombok.RequiredArgsConstructor;
import org.example.project.service.LeetcodeService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class GotTagHandler implements CommandHandler, LeetcodeCommandHandler{

    private final TelegramMessageSender messageSender;
    private final LeetcodeService leetcodeService;
    private final ReplyKeyboardMarkup leetcodeMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        LeetcodeCommandHandlerFactory.setTag(update.getMessage().getText());
        String link = leetcodeService.getProblemWithTagAndDifficulty(
                LeetcodeCommandHandlerFactory.getDifficulty(),
                LeetcodeCommandHandlerFactory.getTag()
        );
        messageSender.sendMessageWithKeyboard(chatId,"Here is your task:\n" + link, leetcodeMenuKeyboard);
    }

    @Override
    public boolean canHandle(String command, LeetcodeMode mode) {
        return mode == LeetcodeMode.TAGS;
    }
}
