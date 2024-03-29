package com.zaza.cleanerexceptionsbot.handlers.impl;

import com.zaza.cleanerexceptionsbot.actions.impl.*;
import com.zaza.cleanerexceptionsbot.handlers.Handler;
import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActionHandler implements Handler {

    private final TelegramClientService clientService;
    private final RegisterAction registerAction;
    private final AddEgsGameAction addEgsGameAction;
    private final AddSteamGameAction addSteamGameAction;
    private final DeleteEgsGameAction deleteEgsGameAction;
    private final DeleteSteamGameAction deleteSteamGameAction;
    private final AddVkGameAction addVkGameAction;
    private final DeleteVkGameAction deleteVkGameAction;
    private final AddUbisoftGameAction addUbisoftGameAction;
    private final DeleteUbisoftGameAction deleteUbisoftGameAction;
    private final AddBattleNetGameAction addBattleNetGameAction;
    private final DeleteBattleNetGameAction deleteBattleNetGameAction;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage() && update.getMessage().getReplyMarkup() == null;
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();

        TelegramClient client = clientService.findOne(chatId);
        String action = client != null ? client.getAction() : null;

        log.info("Handling action from: " + chatId);
        if ("register".equals(action)) {
            registerAction.execute(chatId, text);
        }
        if ("add_egs_game".equals(action)) {
            addEgsGameAction.execute(chatId, text);
        }
        if ("add_steam_game".equals(action)) {
            addSteamGameAction.execute(chatId, text);
        }
        if ("delete_egs_game".equals(action)) {
            deleteEgsGameAction.execute(chatId, text);
        }
        if ("delete_steam_game".equals(action)) {
            deleteSteamGameAction.execute(chatId, text);
        }
        if ("add_vk_game".equals(action)) {
            addVkGameAction.execute(chatId, text);
        }
        if ("delete_vk_game".equals(action)) {
            deleteVkGameAction.execute(chatId, text);
        }
        if ("add_ubisoft_game".equals(action)) {
            addUbisoftGameAction.execute(chatId, text);
        }
        if ("delete_ubisoft_game".equals(action)) {
            deleteUbisoftGameAction.execute(chatId, text);
        }
        if ("add_battlenet_game".equals(action)) {
            addBattleNetGameAction.execute(chatId, text);
        }
        if ("delete_battlenet_game".equals(action)) {
            deleteBattleNetGameAction.execute(chatId, text);
        }
        log.info("Handled action from: " + chatId);
    }
}
