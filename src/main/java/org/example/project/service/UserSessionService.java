package org.example.project.service;

import com.plexpt.chatgpt.entity.chat.Message;
import org.example.project.data.UserSession;
import org.example.project.service.state.DialogMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionService {
    private final ConcurrentHashMap<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public UserSession getSession(long chatId) {
        return userSessions.computeIfAbsent(chatId, k -> new UserSession(DialogMode.MAIN, "", new ArrayList<>()));
    }

    public UserSession setDialogMode(long chatId, DialogMode mode) {
        UserSession session;
        if (!userSessions.containsKey(chatId))
            session = getSession(chatId);
        else
            session = userSessions.get(chatId);
        userSessions.put(chatId, new UserSession(mode, session.getGptVersion(), session.getMessageHistory()));
        return userSessions.get(chatId);
    }

    public void setGptVersion(long chatId, String gptVersion) {
        UserSession session;
        if (!userSessions.containsKey(chatId))
            session = getSession(chatId);
        else
            session = userSessions.get(chatId);
        userSessions.put(chatId, new UserSession(session.getDialogMode(), gptVersion, session.getMessageHistory()));
    }

    public void addMessage(long chatId, Message message) {
        UserSession session;
        if (!userSessions.containsKey(chatId))
            session = getSession(chatId);
        else
            session = userSessions.get(chatId);
        session.getMessageHistory().add(message);
    }
}
