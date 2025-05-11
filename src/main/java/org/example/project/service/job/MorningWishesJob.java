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
public class MorningWishesJob {

    private final UserRepository userRepository;
    private final TelegramMessageSender telegramMessageSender;
    private final ChatGPTService chatGPTService;

    private final String PROMPT = "Напиши пожелание с добрым утром. Это пожелание должно поднимать настроение и " +
            "настраивать на продуктивный день, оно должно быть не сильно большим, но и не сильно маленьким. " +
            "В ответ пришли только пожелание, без лишнего текста";

    @Scheduled(cron = "0 0 6 * * *")
    public void scheduleMorningWishes() {
        var users = userRepository.getAllUsers();

        var message = chatGPTService.sendMessage("", PROMPT, "gpt-3.5-turbo", new ArrayList<>());
        for (var user: users) {
            telegramMessageSender.sendMessage(user.getChatId(), message);
        }
    }
}
