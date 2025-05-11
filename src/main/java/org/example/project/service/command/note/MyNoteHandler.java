package org.example.project.service.command.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.domain.Users;
import org.example.project.service.NoteService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserService;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyNoteHandler implements NoteCommandHandler, CommandHandler {

    private final TelegramMessageSender telegramMessageSender;
    private final UserService userService;
    private final NoteService noteService;
    private final UserSessionService userSessionService;
    private final ReplyKeyboardMarkup noteMenuKeyboard;
    private final ReplyKeyboardMarkup mainMenuKeyboard;

    @Override
    public boolean canHandle(String command, boolean isPreviousAddNote) {
        return command.equals("/mynote");
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        log.info("Got myNote command from {}", chatId);
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            telegramMessageSender.sendMessageWithKeyboard(chatId, "Firstly, please register", mainMenuKeyboard);
            userSessionService.setDialogMode(chatId, DialogMode.MAIN);
        } else {
            var notes = noteService.getNotesByUser(users);
            StringBuilder builder = new StringBuilder();
            builder.append("Your notes: \n\n");
            int k = 1;
            for (var note: notes) {
                builder.append(k++).append(") ");
                builder.append(note.getContent());
                builder.append("\n\n");
            }
            String answer = builder.toString();
            telegramMessageSender.sendMessageWithKeyboard(chatId, answer, noteMenuKeyboard);
        }
    }
}
