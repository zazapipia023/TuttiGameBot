package com.zaza.tuttifruttibot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BotContext {

    private Map<Long, Map<Long, Integer>> inlineConnection;

    public BotContext() {
        inlineConnection = new HashMap<>();
    }

    public void saveData(Long chatId, Long userId, Integer messageId) {
        Map<Long, Integer> userMessageMap = inlineConnection.get(chatId);
        if (userMessageMap == null) {
            userMessageMap = new HashMap<>();
            inlineConnection.put(chatId, userMessageMap);
        }
        userMessageMap.put(userId, messageId);
    }

    public Integer getData(Long chatId, Long userId) {
        Map<Long, Integer> userMessageMap = inlineConnection.get(chatId);
        if (userMessageMap == null) {
            return null;
        }
        return userMessageMap.get(userId);
    }

}
