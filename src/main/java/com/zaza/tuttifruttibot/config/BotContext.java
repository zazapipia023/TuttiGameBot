package com.zaza.tuttifruttibot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BotContext {

    private Map<Long, Integer> inlineConnection;

    public BotContext() {
        inlineConnection = new HashMap<>();
    }

    public void saveData(Long userId, Integer messageId) {
        inlineConnection.put(userId, messageId);
    }

    public Integer getData(Long userId) {
        return inlineConnection.get(userId);
    }

}
