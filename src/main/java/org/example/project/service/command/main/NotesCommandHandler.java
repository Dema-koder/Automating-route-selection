package org.example.project.service.command.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotesCommandHandler implements MainCommandHandler, CommandHandler {

    private final TelegramMessageSender messageSender;
    private final UserSessionService userSessionService;
    private final ReplyKeyboardMarkup noteMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        messageSender.sendMessageWithKeyboard(
                chatId,
                "Here you can do the following things:\n1) Add notes\n2) Edit notes\n3) Delete notes\n4) Check notes",
                noteMenuKeyboard
        );
        userSessionService.setDialogMode(chatId, DialogMode.NOTE);
    }

    @Override
    public boolean canHandle(String command) {
        return command.equals("/notes");
    }
}
