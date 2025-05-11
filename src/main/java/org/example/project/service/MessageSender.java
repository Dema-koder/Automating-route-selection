package org.example.project.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public interface MessageSender {
    void sendMessage(long chatId, String textToSend);
    void sendMessageWithKeyboard(long chatId, String textToSend, ReplyKeyboardMarkup keyboard);
    void sendTypingAction(long chatId);
}
