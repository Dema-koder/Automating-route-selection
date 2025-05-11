package org.example.project.service.command.note;

import lombok.RequiredArgsConstructor;
import org.example.project.domain.Users;
import org.example.project.service.NoteService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserService;
import org.example.project.service.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SaveNoteHandler implements NoteCommandHandler, CommandHandler {

    private final TelegramMessageSender telegramMessageSender;
    private final UserService userService;
    private final NoteService noteService;
    private final ReplyKeyboardMarkup noteMenuKeyboard;

    @Override
    public boolean canHandle(String command, boolean isPreviousAddNote) {
        return isPreviousAddNote;
    }

    @Override
    public void handleCommand(long chatId, Update update) {
        noteReceived(chatId, update.getMessage().getText());
    }

    private void noteReceived(long chatId, String content) {
        Users users = userService.getUserByChatId(chatId);
        noteService.saveNote(users, content);
        telegramMessageSender.sendMessageWithKeyboard(chatId, "Note successfully added", noteMenuKeyboard);
    }
}
