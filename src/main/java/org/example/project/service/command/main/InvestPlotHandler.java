package org.example.project.service.command.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.domain.Users;
import org.example.project.service.StoncksPlotService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserService;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestPlotHandler implements MainCommandHandler, CommandHandler {
    private final ReplyKeyboardMarkup mainMenuKeyboard;
    private final TelegramMessageSender messageSender;
    private final StoncksPlotService stoncksPlotService;

    @Override
    public boolean canHandle(String command) {
        return command.equals("/investplot");
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        String plot = stoncksPlotService.getPlotImage("week");
        messageSender.sendPlotToUser(chatId, plot);
    }
}
