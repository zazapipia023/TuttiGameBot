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
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();

        if (update.getMessage().isSuperGroupMessage()) {
            switch (update.getMessage().getText()) {
                case "/tutti_frutti@idrakG_bot", "/tutti_frutti@colizeum_csa_bot" ->
                        telegramSender.sendMessage(chatId, tuttiService.makeIceCream(update));

                case "/tutti_frutti_top@idrakG_bot", "/tutti_frutti_top@colizeum_csa_bot" ->
                        telegramSender.sendMessage(chatId, tuttiService.makeTop());

                case "/tutti_frutti_sell@idrakG_bot", "/tutti_frutti_sell@colizeum_csa_bot" ->
                        telegramSender.sendMessage(chatId, tuttiService.sellIceCream(update));

                case "/tutti_frutti_check@idrakG_bot", "/tutti_frutti_check@colizeum_csa_bot" ->
                        telegramSender.sendMessage(chatId, tuttiService.getIceCreamValue(update));
            }
        }

    }
}
