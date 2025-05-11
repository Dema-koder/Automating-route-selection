package org.example.project.service.command.note;

import lombok.RequiredArgsConstructor;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class BackToMainCommandHandler implements NoteCommandHandler, CommandHandler {

    private final TelegramMessageSender messageSender;
    private final UserSessionService userSessionService;
    private final ReplyKeyboardMarkup mainMenuKeyboard;

    @Override
    public void handleCommand(long chatId, Update update) {
        messageSender.sendMessageWithKeyboard(chatId, "You returned to main state", mainMenuKeyboard);
        userSessionService.setDialogMode(chatId, DialogMode.MAIN);
    }

    @Override
    public boolean canHandle(String command, boolean isPreviousAddNote) {
        return command.equals("/back");
    }
}
