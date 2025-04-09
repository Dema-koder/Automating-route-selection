package org.example.project.service;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.example.project.configuration.ApplicationConfig;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ChatGPTService {
    private ChatGPT chatGPT;
    final ApplicationConfig config;

    public ChatGPTService(ApplicationConfig config) {
        this.config = config;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("18.199.183.77", 49232));

        this.chatGPT = ChatGPT.builder()
                .apiKey(config.getGptToken())
                .apiHost("https://api.openai.com/")
                .proxy(proxy)
                .build()
                .init();
    }

    public String sendMessage(String prompt, String question, String model, List<Message> history) {
        log.info("Question: {}", question);

        Message system = Message.ofSystem(prompt);
        Message message = Message.of(question);

        history.add(system);
        history.add(message);

        return sendMessagesToChatGPT(model, history);
    }

    private String sendMessagesToChatGPT(String model, List<Message> messageHistory){
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(model)
                .messages(messageHistory)
                .maxTokens(3000)
                .temperature(0.9)
                .build();

        log.info("{} tokens: {}", chatCompletion.getModel(), chatCompletion.countTokens());

        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        messageHistory.add(res);

        return res.getContent();
    }
}
