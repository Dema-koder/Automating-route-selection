package org.example.project.service.command.leetcode;

import lombok.RequiredArgsConstructor;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.job.LeetcodeTags;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class DifficultyHandler implements CommandHandler, LeetcodeCommandHandler {

    private final TelegramMessageSender messageSender;

    @Override
    public void handleCommand(long chatId, Update update) {
        StringBuilder builder = new StringBuilder();
        LeetcodeCommandHandlerFactory.setDifficulty(update.getMessage().getText());
        builder.append("Choose tags from this list: ");
        var tags = LeetcodeTags.getTags();
        int n = tags.size();
        for (int i = 0; i < n - 1; i++) {
            builder.append(tags.get(i));
            builder.append(", ");
        }
        builder.append(tags.get(n - 1));
        builder.append("\n");
        builder.append("Write only one tag");
        messageSender.sendMessage(chatId, builder.toString());
    }

    @Override
    public boolean canHandle(String command, LeetcodeMode mode) {
        return mode == LeetcodeMode.DIFFICULTY;
    }
}
