package com.zaza.tuttifruttibot.bot;

import com.zaza.tuttifruttibot.config.BotContext;
import com.zaza.tuttifruttibot.game.TuttiFruttiService;
import com.zaza.tuttifruttibot.game.TuttiShopService;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import com.zaza.tuttifruttibot.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.zaza.tuttifruttibot.utils.MarkdownEscaper.escape;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupMessageHandler {

    private final TelegramSender telegramSender;
    private final TuttiFruttiService tuttiFruttiService;
    private final TuttiShopService tuttiShopService;
    private final BotContext botContext;

    public void processMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        try {
            switch (messageText) {
                case "/frutti@tfrutti_bot", "/tutti_frutti@colizeum_csa_bot" -> {
                    log.info("Processing /frutti command from chat {}", chatId);
                    String response = tuttiFruttiService.getPlayerData(update.getMessage().getFrom().getId(), update.getMessage().getChatId(), update.getMessage().getFrom().getFirstName());
                    telegramSender.sendMessage(chatId, escape(response), update.getMessage().getFrom().getId(), KeyboardUtils.createGameKeyboard());
                    log.debug("Successfully processed /frutti command");
                }

                case "/frutti_top@tfrutti_bot", "/tutti_frutti_top@colizeum_csa_bot" -> {
                    log.info("Processing /frutti_top command from chat {}", chatId);
                    String response = tuttiFruttiService.makeTop(chatId);
                    telegramSender.sendMessage(chatId, escape(response));
                    log.debug("Successfully processed /frutti_top command");
                }

                case "/frutti_shop@tfrutti_bot", "/tutti_frutti_shop@colizeum_csa_bot" -> {
                    log.info("Processing /frutti_shop command from chat {}", chatId);
                    if (botContext.getData(update.getMessage().getChatId(), update.getMessage().getFrom().getId()) != null) {
                        telegramSender.deleteMessage(chatId, botContext.getData(update.getMessage().getChatId(), update.getMessage().getFrom().getId()));
                    }

                    String response = tuttiShopService.getShopsData(update);
                    telegramSender.sendMessage(chatId, escape(response), update.getMessage().getFrom().getId(), KeyboardUtils.createShopKeyboard());
                    log.debug("Successfully processed /frutti_shop command");
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
            telegramSender.sendMessage(chatId, escape(errorMessage));
            log.warn("Sent error message to chat {}", chatId);
        } catch (Exception ex) {
            log.error("Failed to send error message to chat {}", chatId, ex);
        }
    }

}
