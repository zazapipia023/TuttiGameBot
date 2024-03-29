package com.zaza.cleanerexceptionsbot.handlers.impl;

import com.zaza.cleanerexceptionsbot.handlers.Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateHandler implements Handler {

    private final MessageHandler messageHandler;
    private final ActionHandler actionHandler;

    private Set<Handler> getHandlers() {
        Set<Handler> handlers = new LinkedHashSet<>();

        handlers.add(messageHandler);
        handlers.add(actionHandler);
        log.info("Set handlers for bot");
        return handlers;
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }

    @Override
    public void handle(Update update) {
        try {
            handleUpdate(update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleUpdate(Update update) {
        getHandlers().stream()
                .filter(handler -> handler.supports(update))
                .findFirst()
                .ifPresent(handler -> handler.handle(update));
    }
}
