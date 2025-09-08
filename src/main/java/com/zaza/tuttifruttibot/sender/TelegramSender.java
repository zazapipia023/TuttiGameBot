package com.zaza.cleanerexceptionsbot.sender;

import com.zaza.cleanerexceptionsbot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramSender extends DefaultAbsSender {

    private final BotConfig botConfig;

    @Autowired
    protected TelegramSender(BotConfig botConfig) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendInvoice(SendInvoice sendInvoice) {
        try {
            execute(sendInvoice);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCheckoutQuery(AnswerPreCheckoutQuery checkoutQuery) {
        try {
            execute(checkoutQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
