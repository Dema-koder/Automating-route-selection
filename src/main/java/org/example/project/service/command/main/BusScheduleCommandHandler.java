package org.example.project.service.command.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.parser.Parser;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusScheduleCommandHandler implements MainCommandHandler, CommandHandler {

    private final Parser parser;
    private final TelegramMessageSender messageSender;
    private final ReplyKeyboardMarkup mainMenuKeyboard;

    @Override
    public boolean canHandle(String command) {
        return command.equals("/busschedule");
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        var schedule = parser.getVasilyevoTimes();
        StringBuilder builder = new StringBuilder();
        builder.append("Departure time from Vasilyevo: \n");
        for (var time: schedule) {
            builder.append(time);
            builder.append(" ");
        }
        builder.append("\n\nDeparture time from Kazan: \n");
        schedule = parser.getKazanTimes();
        for (var time: schedule) {
            builder.append(time);
            builder.append(" ");
        }
        messageSender.sendMessageWithKeyboard(chatId, builder.toString(), mainMenuKeyboard);
    }
}
