package org.example.project.service.command.main;

import lombok.extern.slf4j.Slf4j;
import org.example.project.domain.Users;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserService;
import org.example.project.service.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class UnregisterCommandHandler implements MainCommandHandler, CommandHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramMessageSender messageSender;

    @Override
    public boolean canHandle(String command) {
        return command.equals("/unregister");
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            String message = "You are not registered";
            messageSender.sendMessage(chatId, message);
        } else {
            userService.removeUser(users);
            log.info("User {} was deleted", users.getName());
            String message = "Your data was delete";
            messageSender.sendMessage(chatId, message);
        }
    }
}
