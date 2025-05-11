package org.example.project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Slf4j
@Component
public class TelegramMessageSender implements MessageSender{
    private final TelegramBot bot;

    public TelegramMessageSender(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendTypingAction(long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);
        try {
            bot.execute(action);
        } catch (TelegramApiException e) {
            log.warn("Failed to send typing action for chatId: {}", chatId, e);
        }
    }

    public void sendMessage(long chatId, String textToSend) {
        var messages = splitString(textToSend, 4096);
        SendMessage sendMessage = new SendMessage();
        for (var message: messages) {
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error occurred: {}", e.getMessage());
            }
        }
    }

    public void sendMessageWithKeyboard(long chatId, String textToSend, ReplyKeyboardMarkup keyboard) {
        var messages = splitString(textToSend, 4096);
        SendMessage sendMessage = new SendMessage();
        for (var message: messages) {
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setReplyMarkup(keyboard);
            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error occurred: {}", e.getMessage());
            }
        }
    }


    private String[] splitString(String str, int length) {
        if (str == null || length <= 0) {
            return new String[0];
        }

        int partsCount = (int) Math.ceil((double) str.length() / length);
        String[] parts = new String[partsCount];

        for (int i = 0; i < partsCount; i++) {
            int start = i * length;
            int end = Math.min(start + length, str.length());
            parts[i] = str.substring(start, end);
        }

        return parts;

    }

    public void sendPlotToUserWithKeyboard(Long chatId, String htmlResponse, ReplyKeyboardMarkup keyboard) {
        try {
            String base64Prefix = "data:image/png;base64,";
            int startIndex = htmlResponse.indexOf(base64Prefix) + base64Prefix.length();
            int endIndex = htmlResponse.indexOf("\"", startIndex);
            String base64Image = htmlResponse.substring(startIndex, endIndex);

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            InputFile photo = new InputFile(new ByteArrayInputStream(imageBytes), "plot.png");

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .replyMarkup(keyboard)
                    .photo(photo)
                    .caption("График изменения портфеля")
                    .build();

            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
