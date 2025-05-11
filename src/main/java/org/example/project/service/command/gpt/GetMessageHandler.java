package org.example.project.service.command.gpt;

import com.plexpt.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.repository.ChatGptMessagesRepository;
import org.example.project.repository.UserRepository;
import org.example.project.service.ChatGPTService;
import org.example.project.service.TelegramMessageSender;
import org.example.project.service.UserSessionService;
import org.example.project.service.command.CommandHandler;
import org.example.project.service.state.DialogMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.example.project.domain.ChatGptMessages;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetMessageHandler implements CommandHandler, GPTCommandHandler {

    @Autowired
    private TelegramMessageSender messageSender;

    @Autowired
    private ChatGPTService chatGPTService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGptMessagesRepository chatGptMessagesRepository;

    @Override
    public void handleCommand(long chatId, Update update) {
        log.info("Sending message to ChatGPT for chatId: {}", chatId);

        messageSender.sendTypingAction(chatId);
        String question = update.getMessage().getText();
        String model = userSessionService.getSession(chatId).getGptVersion();
        var messageHistory = userSessionService.getSession(chatId).getMessageHistory();

        try {
            String answer = chatGPTService.sendMessage("", question, model, messageHistory);
            messageSender.sendMessage(chatId, answer);
            try {
                saveChatGptMessages(question, answer, chatId);
            } catch (Exception e) {
                log.error("Can not save message to database: ", e);
            }
        } catch (Exception e) {
            log.error("ChatGPT request failed for chatId: {}", chatId, e);
            messageSender.sendMessage(chatId, "⚠️ Произошла ошибка при обработке запроса");
        } finally {
            userSessionService.addMessage(chatId, Message.of(question));
        }
    }

    @Override
    public boolean canHandle(String command) {
        return !command.equals("Exit") && !command.matches("[1-4]");
    }

    private void saveChatGptMessages(String question, String answer, long chatId) {
        Long userId = userRepository.findByChatId(chatId).getId();
        chatGptMessagesRepository.save(ChatGptMessages.builder()
                .question(question)
                .answer(answer)
                .userId(userId)
                .build());
    }
}
