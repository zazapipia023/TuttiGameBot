package com.zaza.cleanerexceptionsbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotConfig {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

}
