package org.example.project.service.command.leetcode;

import lombok.RequiredArgsConstructor;
import org.example.project.service.LeetcodeService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.job.LeetcodeTags;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class GotTagHandler implements CommandHandler, LeetcodeCommandHandler{

    private final TelegramMessageSender messageSender;
    private final LeetcodeService leetcodeService;
    private final ReplyKeyboardMarkup leetcodeMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        String tag = update.getMessage().getText();
        if (!LeetcodeTags.getTags().contains(tag)) {
            messageSender.sendPlotToUserWithKeyboard(
                    chatId,
                    "Sorry, you sent wrong tag",
                    leetcodeMenuKeyboard
            );
            return;
        }
        String link = "";
        try {
            link = leetcodeService.getProblemWithTagAndDifficulty(
                    LeetcodeCommandHandlerFactory.getDifficulty(),
                    tag
            );
        } catch (URISyntaxException | IOException | InterruptedException e) {
            messageSender.sendMessageWithKeyboard(
                    chatId,
                    "Internal server error!",
                    leetcodeMenuKeyboard
            );
        }
        messageSender.sendMessageWithKeyboard(
                chatId,
                "Here is your task:\n" + link,
                leetcodeMenuKeyboard
        );
    }

    @Override
    public boolean canHandle(String command, LeetcodeMode mode) {
        return mode == LeetcodeMode.TAGS;
    }
}
