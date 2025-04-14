package org.example.project.service.job;

import org.example.project.repository.UserRepository;
import org.example.project.service.ChatGPTService;
import org.example.project.service.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MorningWishesJob {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private ChatGPTService chatGPTService;

    private final String PROMPT = "Напиши пожелание с добрым утром. Это пожелание должно поднимать настроение и " +
            "настраивать на продуктивный день, оно должно быть не сильно большим, но и не сильно маленьким. " +
            "В ответ пришли только пожелание, без лишнего текста";

    @Scheduled(cron = "0 0 6 * * *")
    public void scheduleMorningWishes() {
        var users = userRepository.getAllUsers();

        var message = chatGPTService.sendMessage("", PROMPT, "gpt-3.5-turbo", new ArrayList<>());
        for (var user: users) {
            telegramBot.sendMessage(user.getChatId(), message);
        }
    }
}
