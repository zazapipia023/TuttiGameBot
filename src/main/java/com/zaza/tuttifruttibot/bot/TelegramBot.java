package com.zaza.tuttifruttibot.bot;

import com.zaza.tuttifruttibot.config.BotConfig;
import com.zaza.tuttifruttibot.config.BotContext;
import com.zaza.tuttifruttibot.game.TuttiShopService;
import com.zaza.tuttifruttibot.game.TuttiFruttiService;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import com.zaza.tuttifruttibot.upgrades.HardwareEquipment;
import com.zaza.tuttifruttibot.upgrades.IceCreamTypes;
import com.zaza.tuttifruttibot.upgrades.Toppings;
import com.zaza.tuttifruttibot.utils.KeyboardUtils;
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
    private final GroupMessageHandler groupMessageHandler;
    private final CallbackHandler callbackHandler;


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
        if (update.hasCallbackQuery()) {
            log.info("Callback Query received: {} from {}", update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom().getId());
            callbackHandler.processCallback(update);
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.debug("Received update without message or text: {}", update);
            return;
        }

        messageLog(update);

        if (update.getMessage().isSuperGroupMessage()) {
            log.debug("Message is from supergroup");
            groupMessageHandler.processMessage(update);
        } else {
            log.debug("Message is not from supergroup, ignoring");
        }
    }

    private void messageLog(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        String userName = update.getMessage().getFrom().getUserName();
        String firstName = update.getMessage().getFrom().getFirstName();

        log.info("Received message from {} ({}): {} in chat {}",
                userName, firstName, messageText, chatId);
    }
}
