package org.example.project.service;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatChoice;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import org.example.project.configuration.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatGPTServiceTest {
    @Mock
    private ApplicationConfig config;

    @Mock
    private ChatGPT chatGPT;

    @InjectMocks
    private ChatGPTService chatGPTService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(config.getGptToken()).thenReturn("fake-api-key");

        chatGPTService = new ChatGPTService(config, chatGPT);
    }

    @Test
    void testSendMessage() {
        String prompt = "System prompt";
        String question = "What is the capital of France?";

        Message systemMessage = Message.ofSystem(prompt);
        Message userMessage = Message.of(question);
        Message responseMessage = Message.of("The capital of France is Paris.");

        // Mock the ChatChoice
        ChatChoice chatChoice = mock(ChatChoice.class);
        when(chatChoice.getMessage()).thenReturn(responseMessage);

        // Mock the ChatCompletionResponse
        ChatCompletionResponse mockResponse = mock(ChatCompletionResponse.class);
        when(mockResponse.getChoices()).thenReturn(Arrays.asList(chatChoice));

        when(chatGPT.chatCompletion(any(ChatCompletion.class))).thenReturn(mockResponse);

        String result = chatGPTService.sendMessage(prompt, question);

        assertEquals("The capital of France is Paris.", result);
        verify(chatGPT, times(1)).chatCompletion(any(ChatCompletion.class));
    }
}