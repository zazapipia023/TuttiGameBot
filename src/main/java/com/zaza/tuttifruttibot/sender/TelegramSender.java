package com.zaza.tuttifruttibot.sender;

import com.zaza.tuttifruttibot.config.BotConfig;
import com.zaza.tuttifruttibot.config.BotContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramSender extends DefaultAbsSender {

    private final BotConfig botConfig;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final BotContext botContext;

    @Autowired
    protected TelegramSender(BotConfig botConfig, BotContext botContext) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
        this.botContext = botContext;
        log.info("TelegramSender initialized");
    }

    @Override
    public String getBotToken() {
        log.trace("Getting bot token");
        return botConfig.getBotToken();
    }

    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.MARKDOWNV2);
        log.info("Sending message to user, chatId: {}", chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDeletingMessage(Long chatId, String message, Integer userMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.MARKDOWNV2);

        log.info("Sending message with delete mark, chatId: {}", chatId);
        try {
            Message sentMessage = execute(sendMessage);
            scheduleMessageDeletion(sentMessage.getChatId(), sentMessage.getMessageId());
            scheduleMessageDeletion(sentMessage.getChatId(), userMessageId);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long chatId, String message, Long userId, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setParseMode(ParseMode.MARKDOWNV2);
        log.info("Sending message to user with markup: {}", userId);
        try {
            Message sentMessage = execute(sendMessage);
            Integer messageId = sentMessage.getMessageId();
            botContext.saveData(userId, messageId);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMessageWithMarkup(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setReplyMarkup(markup);
        editMessage.setParseMode(ParseMode.MARKDOWNV2);

        log.info("Editing message with markup, messageId: {}, text: {}", messageId, text);
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAlertResponse(String callbackQueryId, String message) {
        AnswerCallbackQuery response = new AnswerCallbackQuery();
        response.setText(message);
        response.setShowAlert(true);
        response.setCallbackQueryId(callbackQueryId);
        log.info("Sending Alert Response, callbackQueryId: {}, message: {}", callbackQueryId, message);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNotYourMessageResponse(String callbackQueryId) {
        AnswerCallbackQuery response = new AnswerCallbackQuery();
        response.setText("Эти кнопки не для тебя!");
        response.setShowAlert(true);
        response.setCallbackQueryId(callbackQueryId);

        log.info("Sending \"Not your message\" callback response");
        try {
            execute(response);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(Long chatId, Integer userMessageId) {
        log.info("Deleting message with id {}", userMessageId);
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(userMessageId);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleMessageDeletion(Long chatId, Integer messageId) {
        scheduler.schedule(() -> {
            try {
                log.info("Schedule deleting message with id {}", messageId);
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId.toString());
                deleteMessage.setMessageId(messageId);
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                log.error("Error while deleting message: {}", e.getMessage());
            }
        }, 1, TimeUnit.MINUTES);
    }
}
