package com.zaza.cleanerexceptionsbot.commands.impl;

import com.zaza.cleanerexceptionsbot.commands.Command;
import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteBattleNetGameCommand implements Command<Long> {

    private final TelegramSender telegramSender;
    private final TelegramClientService clientService;

    @Override
    public void execute(Long chatId) {
        setAction(chatId);
        sendMessage(chatId);
        log.info("Sent message to: " + chatId);
    }

    private void sendMessage(Long chatId) {
        String message = "Напишите название папки с игрой";
        telegramSender.sendMessage(chatId, message);
    }

    private void setAction(Long chatId) {
        log.info("Setting action \"delete_battlenet_game\" to user: " + chatId);
        TelegramClient client = clientService.findOne(chatId);
        client.setAction("delete_battlenet_game");
        clientService.save(client);
        log.info("Saved action \"delete_battlenet_game\" to user: " + chatId);
    }
}
