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
public class RegisterCommand implements Command<Long> {

    private final TelegramSender telegramSender;
    private final TelegramClientService clientService;

    @Override
    public void execute(Long chatId) {
        setAction(chatId);
        sendMessage(chatId);
        log.info("Sent message to: " + chatId);
    }

    private void sendMessage(Long chatId) {
        String message = "Напишите ID вашего клуба";
        telegramSender.sendMessage(chatId, message);
    }

    private void setAction(Long chatId) {
        log.info("Setting action \"register\" to user: " + chatId);
        TelegramClient client = clientService.findOne(chatId);
        client.setAction("register");
        clientService.save(client);
        log.info("Saved action \"register\" to user: " + chatId);
    }
}
