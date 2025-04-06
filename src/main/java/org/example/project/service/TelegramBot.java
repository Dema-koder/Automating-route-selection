package org.example.project.service;

import lombok.extern.slf4j.Slf4j;
import org.example.project.configuration.ApplicationConfig;
import org.example.project.domain.Users;
import org.example.project.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private Parser parser;

    @Autowired
    private ChatGPTService chatGPTService;

    @Autowired
    private IPService ipService;

    final ApplicationConfig config;

    private DialogMode dialogMode;

    static final String HELP_TEXT = "This bot is created by Demyan Zverev and for his own purpose\n\n" +
            "You can execute from the main menu on the left or by typing command: \n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /help to see a description of every command\n\n" +
            "Type /addnote to add note\n\n" +
            "Type /mynote to see all your notes\n\n" +
            "Type /unregister to unregister from the bot, all your notes was deleted\n\n" +
            "Type /busschedule to check schedule of bus from Vasilyevo to Kazan or vice verse\n\n" +
            "Type /gpt to get answer on your question from ChatGpt";

    public TelegramBot(ApplicationConfig config) {
        this.config = config;
        this.dialogMode = DialogMode.MAIN;
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "get a welcome message"));
        listOfCommand.add(new BotCommand("/unregister", "delete my data"));
        listOfCommand.add(new BotCommand("/help", "get description of every command"));
        listOfCommand.add(new BotCommand("/addnote", "add new note"));
        listOfCommand.add(new BotCommand("/mynote", "get your notes"));
        listOfCommand.add(new BotCommand("/busschedule", "check schedule of buses"));
        listOfCommand.add(new BotCommand("/gpt", "get answer from ChatGpt"));
        listOfCommand.add(new BotCommand("/ip", "get your current ip"));
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

            switch(dialogMode) {
                case MAIN:
                    switch (messageText) {
                        case "/start":
                            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                            break;
                        case "/help":
                            helpReceived(chatId);
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
                        case "/busschedule":
                            busScheduleReceived(chatId);
                            break;
                        case "/gpt":
                            gptReceived(chatId);
                            break;
                        case "/ip":
                            getIP(chatId);
                            break;
                        default:
                            wrongCommandReceived(chatId);
                            break;
                    }
                    break;
                case ADD_NOTE:
                    noteReceived(chatId, messageText);
                    break;
                case WAIT_DEPARTURE_PLACE:
                    sendBusSchedule(chatId, messageText);
                    break;
                case WAIT_QUESTION:
                    questionToGptReceived(chatId, messageText);
                    break;
            }
        }
    }

    // TODO: Изменить взаимодействие с ChatGPT в боте
    private void questionToGptReceived(long chatId, String question) {
        log.info("Send message to ChatGPT");
        String answer = chatGPTService.sendMessage("", question);
        sendMessage(chatId, answer);
        dialogMode = DialogMode.MAIN;
    }

    private void gptReceived(long chatId) {
        String answer = "Write your question:";
        sendMessage(chatId, answer);
        dialogMode = DialogMode.WAIT_QUESTION;
    }

    private void getIP(long chatId) {
        String answer = "Your current IP: ";
        try {
            answer += ipService.getIP();
        } catch (NullPointerException e) {
            answer = e.getMessage();
            log.error("Exception: %s".formatted(e.getMessage()));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendMessage(chatId, answer);
        dialogMode = DialogMode.MAIN;
    }

    private void sendBusSchedule(long chatId, String variant) {
        switch (variant) {
            case "1":
                var schedule = parser.getVasilyevoTimes();
                StringBuilder builder = new StringBuilder();
                for (var time: schedule) {
                    builder.append(time);
                    builder.append(" ");
                }
                String answer = "Departure time from Vasilyevo: \n" +
                        builder;
                sendMessage(chatId, answer);
                break;
            case "2":
                schedule = parser.getKazanTimes();
                builder = new StringBuilder();
                for (var time: schedule) {
                    builder.append(time);
                    builder.append(" ");
                }
                answer = "Departure time from Kazan: \n" +
                        builder;
                sendMessage(chatId, answer);
                break;
            default:
                answer = "You chose wrong variant";
                sendMessage(chatId, answer);
                break;
        }
        dialogMode = DialogMode.MAIN;
    }

    private void busScheduleReceived(long chatId) {
        String answer = "Please select from there you need schedule: \n\n" +
                "1. From Vasilyevo \n\n" +
                "2. From Kazan\n\n" +
                "Send only one digit(1 or 2):";
        sendMessage(chatId, answer);
        dialogMode = DialogMode.WAIT_DEPARTURE_PLACE;
    }

    private void myNoteReceived(long chatId) {
        log.info("Got myNote command from {}", chatId);
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            sendMessage(chatId, "Firstly, please register");
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
            sendMessage(chatId, answer);
        }
    }

    private void noteReceived(long chatId, String content) {
        Users users = userService.getUserByChatId(chatId);
        noteService.saveNote(users, content);
        sendMessage(chatId, "Note successfully added");
        dialogMode = DialogMode.MAIN;
    }

    private void addNoteReceived(long chatId) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            sendMessage(chatId, "Firstly, please register");
        } else {
            String message = "Write your note";
            sendMessage(chatId, message);
            dialogMode = DialogMode.ADD_NOTE;
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

    private void helpReceived(long chatId) {
        log.info("Help command received from user {}", chatId);
        sendMessage(chatId, HELP_TEXT);
    }

    private void wrongCommandReceived(long chatId) {
        log.info("Wrong command received from user {}", chatId);
        sendMessage(chatId, "Sorry, command was not recognized");
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
        var messages = splitString(textToSend, 4096);
        SendMessage sendMessage = new SendMessage();
        for (var message: messages) {
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error occurred: {}", e.getMessage());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
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
}
