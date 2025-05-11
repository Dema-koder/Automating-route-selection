package org.example.project.service.command.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.domain.Users;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserService;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommandHandler implements MainCommandHandler, CommandHandler {

    private final ReplyKeyboardMarkup mainMenuKeyboard;
    private final UserService userService;
    private final TelegramMessageSender messageSender;

    @Override
    public boolean canHandle(String command) {
        return command.equals("/start");
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        Users users = userService.getUserByChatId(chatId);
        String name = update.getMessage().getChat().getFirstName();
        if (users == null) {
            userService.addUser(chatId, name);
            String answer = "Hi, " + name + ", nice to meet you!";
            log.info("Replied to user {} and register him", name);
            messageSender.sendMessageWithKeyboard(chatId, answer, mainMenuKeyboard);
        } else {
            String answer = "Hi, " + users.getName() + ", welcome back!";
            log.info("Replied to yet registered user {}", name);
            messageSender.sendMessageWithKeyboard(chatId, answer, mainMenuKeyboard);
        }
    }
}
