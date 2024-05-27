package org.example.project.service;

import lombok.extern.slf4j.Slf4j;
import org.example.project.configuration.ApplicationConfig;
import org.example.project.domain.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    private boolean needAddNote;

    final ApplicationConfig config;

    static final String HELP_TEXT = "This bot is created by Demyan Zverev and for his own purpose\n\n" +
            "You can execute from the main menu on the left or by typing command: \n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /help to see a description of every command\n\n" +
            "Type /addnote to add note\n\n" +
            "Type /mynote to see all your notes\n\n" +
            "Type /unregister to unregister from the bot, all your notes was deleted";

    public TelegramBot(ApplicationConfig config) {
        this.config = config;
        this.needAddNote = false;
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "get a welcome message"));
        listOfCommand.add(new BotCommand("/unregister", "delete my data"));
        listOfCommand.add(new BotCommand("/help", "get description of every command"));
        listOfCommand.add(new BotCommand("/addnote", "add new note"));
        listOfCommand.add(new BotCommand("/mynote", "get your notes"));
        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/unregister":
                    unregisterReceived(chatId);
                    break;
                case "/addnote":
                    addNoteReceived(chatId);
                    break;
                case "/mynote":
                    myNoteReceived(chatId);
                    break;
                default:
                    if (needAddNote) {
                        noteReceived(chatId, update.getMessage().getText());
                        sendMessage(chatId, "Note successfully added");
                        needAddNote = false;
                    } else {
                        sendMessage(chatId, "Sorry, command was not recognized");
                    }
                    break;
            }
        }
    }

    private void myNoteReceived(long chatId) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            sendMessage(chatId, "Firstly, please register");
        } else {
            var notes = noteService.getNotesByUser(users);
            StringBuilder builder = new StringBuilder();
            builder.append("Your notes: \n\n");
            for (var note: notes) {
                builder.append(note.getContent());
                builder.append("\n\n");
            }
            String answer = builder.toString();
            sendMessage(chatId, answer);
        }
    }

    private void noteReceived(long chatId, String content) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            sendMessage(chatId, "Firstly, please register");
        } else {
            noteService.saveNote(users, content);
        }
    }

    private void addNoteReceived(long chatId) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            sendMessage(chatId, "Firstly, please register");
        } else {
            String message = "Write your note";
            sendMessage(chatId, message);
            needAddNote = true;
        }
    }

    private void unregisterReceived(long chatId) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            String message = "You are not registered";
            sendMessage(chatId, message);
        } else {
            userService.removeUser(users);
            log.info("User {} was deleted", users.getName());
            String message = "Your data was delete";
            sendMessage(chatId, message);
        }
    }

    private void startCommandReceived(long chatId, String name) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            userService.addUser(chatId, name);
            String answer = "Hi, " + name + ", nice to meet you!";
            log.info("Replied to user {} and register him", name);
            sendMessage(chatId, answer);
        } else {
            String answer = "Hi, " + users.getName() + ", welcome back!";
            log.info("Replied to yet registered user {}", name);
            sendMessage(chatId, answer);
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
