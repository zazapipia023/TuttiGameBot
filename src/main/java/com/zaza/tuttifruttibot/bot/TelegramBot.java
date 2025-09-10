package com.zaza.tuttifruttibot.bot;

import com.zaza.tuttifruttibot.config.BotConfig;
import com.zaza.tuttifruttibot.game.TuttiFruttiService;
import com.zaza.tuttifruttibot.sender.TelegramSender;
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
    private final TelegramSender telegramSender;
    private final TuttiFruttiService tuttiService;


    @Override
    public String getBotUsername() {
        log.debug("Getting bot username: {}", botConfig.getBotName());
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        log.trace("Getting bot token");
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.debug("Received update without message or text: {}", update);
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        String userName = update.getMessage().getFrom().getUserName();
        String firstName = update.getMessage().getFrom().getFirstName();

        log.info("Received message from {} ({}): {} in chat {}",
                userName, firstName, messageText, chatId);

        if (update.getMessage().isSuperGroupMessage()) {
            log.debug("Message is from supergroup: {}", chatId);
            processGroupMessage(update, chatId, messageText);
        } else {
            log.debug("Message is not from supergroup, ignoring: {}", chatId);
        }
    }

    private void processGroupMessage(Update update, Long chatId, String messageText) {
        try {
            switch (messageText) {
                case "/tutti_frutti@idrakG_bot", "/tutti_frutti@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti command from chat {}", chatId);
                    String response = tuttiService.makeIceCream(update);
                    telegramSender.sendMessage(chatId, response, update.getMessage().getMessageId());
                    log.debug("Successfully processed /tutti_frutti command");
                }

                case "/tutti_frutti_top@idrakG_bot", "/tutti_frutti_top@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_top command from chat {}", chatId);
                    String response = tuttiService.makeTop();
                    telegramSender.sendMessage(chatId, response, update.getMessage().getMessageId());
                    log.debug("Successfully processed /tutti_frutti_top command");
                }

                case "/tutti_frutti_sell@idrakG_bot", "/tutti_frutti_sell@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_sell command from chat {}", chatId);
                    String response = tuttiService.sellIceCream(update);
                    telegramSender.sendMessage(chatId, response, update.getMessage().getMessageId());
                    log.debug("Successfully processed /tutti_frutti_sell command");
                }

                case "/tutti_frutti_check@idrakG_bot", "/tutti_frutti_check@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_check command from chat {}", chatId);
                    String response = tuttiService.getIceCreamValue(update);
                    telegramSender.sendMessage(chatId, response, update.getMessage().getMessageId());
                    log.debug("Successfully processed /tutti_frutti_check command");
                }

                default -> {
                    log.debug("Unknown command received: {}", messageText);
                }
            }
        } catch (Exception e) {
            log.error("Error processing command: {} in chat {}", messageText, chatId, e);
            handleProcessingError(chatId, e, update.getMessage().getMessageId());
        }
    }

    private void handleProcessingError(Long chatId, Exception e, Integer userMessageId) {
        try {
            String errorMessage = "Произошла ошибка при обработке команды. Попробуйте позже.";
            telegramSender.sendMessage(chatId, errorMessage, userMessageId);
            log.warn("Sent error message to chat {}", chatId);
        } catch (Exception ex) {
            log.error("Failed to send error message to chat {}", chatId, ex);
        }
    }
}
