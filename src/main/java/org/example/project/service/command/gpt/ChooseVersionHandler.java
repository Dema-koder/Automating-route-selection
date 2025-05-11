package org.example.project.service.command.gpt;

import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import lombok.RequiredArgsConstructor;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class ChooseVersionHandler implements CommandHandler, GPTCommandHandler {

    private final TelegramMessageSender messageSender;
    private final UserSessionService userSessionService;
    private final ReplyKeyboardMarkup gptKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        String answer = "Write your question:";
        messageSender.sendMessageWithKeyboard(chatId, answer, gptKeyboard);
        String gptVersion = update.getMessage().getText();
        userSessionService.setGptVersion(chatId, whichGptVersion(gptVersion));
    }

    @Override
    public boolean canHandle(String command) {
        return command.matches("[1-4]");
    }

    private String whichGptVersion(String version) {
        return switch (version) {
            case "2" -> ChatCompletion.Model.GPT4;
            case "3" -> ChatCompletion.Model.GPT4oMini;
            case "4" -> ChatCompletion.Model.GPT4o;
            default -> ChatCompletion.Model.GPT_3_5_TURBO;
        };
    }
}
