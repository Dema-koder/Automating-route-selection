package org.example.project.service.command.invest;

import lombok.RequiredArgsConstructor;
import org.example.project.service.StoncksPlotService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GotPeriodHandler implements InvestCommandHandler, CommandHandler {
    private final TelegramMessageSender messageSender;
    private final StoncksPlotService stoncksPlotService;
    private final ReplyKeyboardMarkup investMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        String plot = stoncksPlotService.getPlotImage(update.getMessage().getText());
        messageSender.sendPlotToUserWithKeyboard(chatId, plot, investMenuKeyboard);
    }

    @Override
    public boolean canHandle(String command) {
        Set<String> set = new HashSet<>();
        set.add("15min");
        set.add("30min");
        set.add("1h");
        set.add("3h");
        set.add("6h");
        set.add("12h");
        set.add("day");
        set.add("week");
        set.add("month");
        set.add("year");
        return set.contains(command);
    }
}
