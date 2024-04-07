package com.zaza.cleanerexceptionsbot.handlers.impl;

import com.zaza.cleanerexceptionsbot.handlers.Handler;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckoutQueryHandler implements Handler {

    private final TelegramSender telegramSender;

    @Override
    public boolean supports(Update update) {
        return update.getPreCheckoutQuery() != null;
    }

    @Override
    public void handle(Update update) {
        AnswerPreCheckoutQuery checkoutQuery = new AnswerPreCheckoutQuery();
        checkoutQuery.setPreCheckoutQueryId(update.getPreCheckoutQuery().getId());
        checkoutQuery.setOk(true);

        telegramSender.sendCheckoutQuery(checkoutQuery);
    }
}
