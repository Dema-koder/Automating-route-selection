package org.example.project.service;

import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.example.project.configuration.ApplicationConfig;
import org.example.project.data.UserSession;
import org.example.project.domain.ChatGptMessages;
import org.example.project.domain.Users;
import org.example.project.parser.Parser;
import org.example.project.repository.ChatGptMessagesRepository;
import org.example.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Autowired
    private BotKeyboard botKeyboard;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGptMessagesRepository chatGptMessagesRepository;

    final ApplicationConfig config;

    private final ConcurrentHashMap<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

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
        this.executorService = Executors.newFixedThreadPool(10);
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "get a welcome message"));
        listOfCommand.add(new BotCommand("/unregister", "delete my data"));
        listOfCommand.add(new BotCommand("/help", "get description of every command"));
        listOfCommand.add(new BotCommand("/addnote", "add new note"));
        listOfCommand.add(new BotCommand("/mynote", "get your notes"));
        listOfCommand.add(new BotCommand("/busschedule", "check schedule of buses"));
        listOfCommand.add(new BotCommand("/gpt", "get answer from ChatGpt"));
        listOfCommand.add(new BotCommand("/ip", "get your current ip"));
        listOfCommand.add(new BotCommand("/test", "test"));
        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: %s".formatted(e.getMessage()));
        }
    }

    private UserSession getSession(long chatId) {
        return userSessions.computeIfAbsent(chatId, k -> new UserSession(DialogMode.MAIN, "", new ArrayList<>()));
    }

    @Override
    public void onUpdateReceived(Update update) {
        executorService.submit(() -> executeUpdate(update));
    }

    private void executeUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            var session = getSession(chatId);
            var dialogMode = session.getDialogMode();

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
                            if (userRepository.findByChatId(chatId) == null) {
                                sendMessage(chatId, "Please, register first");
                                getSession(chatId).setDialogMode(DialogMode.MAIN);
                                break;
                            }
                            gptReceived(chatId);
                            break;
                        case "/ip":
                            getIP(chatId);
                            break;
                        case "/test":
                            checkKeyboard(chatId);
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
                case WAIT_GPT_VERSION:
                    session.setGptVersion(gptVersionReceived(chatId, messageText));
                    break;
                case WAIT_QUESTION:
                    if (messageText.equals("Exit")) {
                        getSession(chatId).setDialogMode(DialogMode.MAIN);
                        break;
                    }
                    questionToGptReceived(chatId, messageText, session.getGptVersion(), session.getMessageHistory());
                    break;
            }
        }
    }

    // TODO: Изменить взаимодействие с ChatGPT в боте
    private void questionToGptReceived(long chatId, String question, String model, List<Message> messageHistory) {
        log.info("Sending message to ChatGPT for chatId: {}", chatId);

        sendTypingAction(chatId);

        executorService.submit(() -> {
            try {
                String answer = chatGPTService.sendMessage("", question, model, messageHistory);
                sendMessage(chatId, answer);
                executorService.submit(() -> {
                    try {
                        saveChatGptMessages(question, answer, chatId);
                    } catch (Exception e) {
                        log.error("Can not save message to database: ", e);
                    }
                });
            } catch (Exception e) {
                log.error("ChatGPT request failed for chatId: {}", chatId, e);
                sendMessage(chatId, "⚠️ Произошла ошибка при обработке запроса");
            } finally {
                getSession(chatId).setDialogMode(DialogMode.WAIT_QUESTION);
            }
        });
    }

    private void saveChatGptMessages(String question, String answer, long chatId) {
        Long userId = userRepository.findByChatId(chatId).getId();
        chatGptMessagesRepository.save(ChatGptMessages.builder()
                .question(question)
                .answer(answer)
                .userId(userId)
                .build());
    }

    private void sendTypingAction(long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);
        try {
            execute(action);
        } catch (TelegramApiException e) {
            log.warn("Failed to send typing action for chatId: {}", chatId, e);
        }
    }

    private void gptReceived(long chatId) {
        String answer = """
                Choose version of ChatGPT:
                1) GPT 3.5 Turbo
                2) GPT 4
                3) GPT 4o mini
                4) GPT 4o
                Print only digit""";
        sendMessageWithKeyboard(chatId, answer, botKeyboard.createGptVersionKeyboard());
        getSession(chatId).setDialogMode(DialogMode.WAIT_GPT_VERSION);
    }

    private String gptVersionReceived(long chatId, String gptVersion) {
        String answer = "Write your question:";
        sendMessageWithKeyboard(chatId, answer, botKeyboard.createGptKeyboard());
        getSession(chatId).setDialogMode(DialogMode.WAIT_QUESTION);
        return whichGptVersion(gptVersion);
    }

    private String whichGptVersion(String version) {
        return switch (version) {
            case "2" -> ChatCompletion.Model.GPT4;
            case "3" -> ChatCompletion.Model.GPT4oMini;
            case "4" -> ChatCompletion.Model.GPT4o;
            default -> ChatCompletion.Model.GPT_3_5_TURBO;
        };
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
        getSession(chatId).setDialogMode(DialogMode.MAIN);
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
        getSession(chatId).setDialogMode(DialogMode.MAIN);
    }

    private void busScheduleReceived(long chatId) {
        String answer = "Please select from there you need schedule: \n\n" +
                "1. From Vasilyevo \n\n" +
                "2. From Kazan\n\n" +
                "Send only one digit(1 or 2):";
        sendMessage(chatId, answer);
        getSession(chatId).setDialogMode(DialogMode.WAIT_DEPARTURE_PLACE);
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
        getSession(chatId).setDialogMode(DialogMode.MAIN);
    }

    private void addNoteReceived(long chatId) {
        Users users = userService.getUserByChatId(chatId);
        if (users == null) {
            sendMessage(chatId, "Firstly, please register");
        } else {
            String message = "Write your note";
            sendMessage(chatId, message);
            getSession(chatId).setDialogMode(DialogMode.ADD_NOTE);
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
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error occurred: {}", e.getMessage());
            }
        }
    }

    private void sendMessageWithKeyboard(long chatId, String textToSend, ReplyKeyboardMarkup keyboard) {
        var messages = splitString(textToSend, 4096);
        SendMessage sendMessage = new SendMessage();
        for (var message: messages) {
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            sendMessage.setReplyMarkup(keyboard);
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

    private void checkKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");
        message.setReplyMarkup(botKeyboard.createMainMenuKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }
}
