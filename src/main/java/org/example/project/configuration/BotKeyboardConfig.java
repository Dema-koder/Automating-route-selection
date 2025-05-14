package org.example.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BotKeyboardConfig {
    @Bean
    public ReplyKeyboardMarkup mainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/notes"));
        row1.add(new KeyboardButton("/gpt"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/busschedule"));
        row2.add(new KeyboardButton("/unregister"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("/leetcode"));
        row3.add(new KeyboardButton("/invest"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    @Bean
    public ReplyKeyboardMarkup gptVersionKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("1"));
        row1.add(new KeyboardButton("2"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("3"));
        row2.add(new KeyboardButton("4"));

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    @Bean
    public ReplyKeyboardMarkup gptKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Exit"));

        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    @Bean
    public ReplyKeyboardMarkup noteMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/addnote"));
        row1.add(new KeyboardButton("/mynote"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/editnote (todo)"));
        row2.add(new KeyboardButton("/deletenote (todo)"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("/back"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    @Bean
    public ReplyKeyboardMarkup leetcodeMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/randomtask"));
        row1.add(new KeyboardButton("/randomtaskwith"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/back"));

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    @Bean
    public ReplyKeyboardMarkup difficultyMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Easy"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Medium"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Hard"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }

    @Bean
    public ReplyKeyboardMarkup investMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("15min"));
        row1.add(new KeyboardButton("30min"));
        row1.add(new KeyboardButton("1h"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("3h"));
        row2.add(new KeyboardButton("6h"));
        row2.add(new KeyboardButton("12h"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("day"));
        row3.add(new KeyboardButton("week"));
        row3.add(new KeyboardButton("month"));

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("year"));
        row4.add(new KeyboardButton("/back"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }
}
