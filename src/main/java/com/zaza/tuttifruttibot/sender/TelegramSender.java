package com.zaza.tuttifruttibot.sender;

import com.zaza.tuttifruttibot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramSender extends DefaultAbsSender {

    private final BotConfig botConfig;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    protected TelegramSender(BotConfig botConfig) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
        log.info("TelegramSender initialized");
    }

    @Override
    public String getBotToken() {
        log.trace("Getting bot token");
        return botConfig.getBotToken();
    }

    public void sendMessage(Long chatId, String message, Integer userMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            Message sentMessage = execute(sendMessage);
            scheduleMessageDeletion(sentMessage.getChatId(), sentMessage.getMessageId());
            scheduleMessageDeletion(sentMessage.getChatId(), userMessageId);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleMessageDeletion(Long chatId, Integer messageId) {
        scheduler.schedule(() -> {
            try {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId.toString());
                deleteMessage.setMessageId(messageId);
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                System.err.println("Не удалось удалить сообщение: " + e.getMessage());
            }
        }, 1, TimeUnit.MINUTES);
    }
}
