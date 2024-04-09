package com.zaza.cleanerexceptionsbot.bot;

import com.zaza.cleanerexceptionsbot.config.BotConfig;
import com.zaza.cleanerexceptionsbot.handlers.impl.UpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final UpdateHandler updateHandler;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (updateHandler.supports(update)) {
            log.info("Handling update: " + update.getUpdateId() + "\n" + update);
            updateHandler.handle(update);
            log.info("Handled update: " + update.getUpdateId());
        }

    }
}
