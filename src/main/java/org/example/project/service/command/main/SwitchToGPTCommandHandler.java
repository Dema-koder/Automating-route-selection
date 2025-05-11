package org.example.project.service.command.main;

import lombok.RequiredArgsConstructor;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SwitchToGPTCommandHandler implements CommandHandler, MainCommandHandler {

    private final ReplyKeyboardMarkup gptVersionKeyboard;
    private final TelegramMessageSender messageSender;
    private final UserSessionService userSessionService;

    @Override
    public void handleCommand(long chatId, Update update) {
        String answer = """
                Choose version of ChatGPT:
                1) GPT 3.5 Turbo
                2) GPT 4
                3) GPT 4o mini
                4) GPT 4o
                Print only digit""";
        messageSender.sendMessageWithKeyboard(chatId, answer, gptVersionKeyboard);
        userSessionService.setDialogMode(chatId, DialogMode.GPT);
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("/gpt");
    }
}
