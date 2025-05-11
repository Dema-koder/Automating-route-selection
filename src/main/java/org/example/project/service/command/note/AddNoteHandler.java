package org.example.project.service.command.note;

import lombok.RequiredArgsConstructor;
import org.example.project.domain.Users;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserService;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class AddNoteHandler implements NoteCommandHandler, CommandHandler {

    private final TelegramMessageSender telegramMessageSender;
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final ReplyKeyboardMarkup mainMenuKeyboard;

    @Override
    public boolean canHandle(String command, boolean flag) {
        return command.equals("/addnote");
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        addNoteReceived(chatId);
    }

    private void addNoteReceived(long chatId) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            telegramMessageSender.sendMessageWithKeyboard(chatId, "Firstly, please register", mainMenuKeyboard);
            userSessionService.setDialogMode(chatId, DialogMode.MAIN);
        } else {
            String message = "Write your note";
            telegramMessageSender.sendMessage(chatId, message);
        }
    }
}
