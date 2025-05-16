package org.example.project.service.job;

import lombok.RequiredArgsConstructor;
import org.example.project.repository.UserRepository;
import org.example.project.service.ChatGPTService;
import org.example.project.service.TelegramMessageSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class IntrestingFactsJob {
    private final UserRepository userRepository;
    private final TelegramMessageSender telegramMessageSender;
    private final ChatGPTService chatGPTService;

    private final String PROMPT = "Напиши короткий интересный факт из истории России. Не выдумывай его, возьми реальный";

    @Scheduled(cron = "0 0 9 * * *")
    public void scheduleMorningWishes() {
        var users = userRepository.getAllUsers();

        var message = chatGPTService.sendMessage("", PROMPT, "gpt-3.5-turbo", new ArrayList<>());
        for (var user: users) {
            telegramMessageSender.sendMessage(user.getChatId(), message);
        }
    }
}
