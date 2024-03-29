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
public class StartCommand implements Command<Long> {

    private final TelegramSender sender;
    private final TelegramClientService clientService;

    @Override
    public void execute(Long chatId) {
        TelegramClient client = clientService.findOne(chatId);
        if (client == null) {
            saveUser(chatId);
            sendStartMessage(chatId);
        } else {
            sendStartMessage(chatId);
        }
        log.info("Sent message to: " + chatId);
    }

    private void sendStartMessage(Long chatId) {
        String message = "Для регистрации клуба напишите /register";
        sender.sendMessage(chatId, message);
    }

    private void saveUser(Long chatId) {
        log.info("Saving user " + chatId);
        TelegramClient client = new TelegramClient();
        client.setId(chatId);
        client.setAction("none");
        clientService.save(client);
        log.info("Saved user " + chatId);
    }
}
