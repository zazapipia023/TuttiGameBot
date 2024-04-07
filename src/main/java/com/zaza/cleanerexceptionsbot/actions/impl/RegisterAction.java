package com.zaza.cleanerexceptionsbot.actions.impl;

import com.zaza.cleanerexceptionsbot.actions.Action;
import com.zaza.cleanerexceptionsbot.models.mongo.Club;
import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import com.zaza.cleanerexceptionsbot.services.ClubService;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import com.zaza.cleanerexceptionsbot.util.DefaultGames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterAction implements Action {

    private final TelegramSender telegramSender;
    private final TelegramClientService clientService;
    private final ClubService clubService;

    @Override
    public void execute(Long chatId, String text) {
        register(chatId, text);
        setAction(chatId);
    }

    private void register(Long chatId, String clubId) {
        if (clubService.findOne(clubId) == null) {
            log.info("Starting registration to user: " + chatId);
            TelegramClient client = clientService.findOne(chatId);
            Club registerClub = new Club();
            registerClub.setClubId(clubId);
            registerClub.setConnectedClient(chatId);
            registerClub.setEgsGames(DefaultGames.egsGames);
            registerClub.setSteamGames(DefaultGames.steamGames);
            registerClub.setVkGames(DefaultGames.vkGames);
            registerClub.setUbisoftGames(Collections.emptyList());
            registerClub.setBattleNetGames(DefaultGames.battleNetGames);
            registerClub.setSubDate(LocalDateTime.now());

            client.setClubId(clubId);

            clientService.save(client);
            clubService.save(registerClub);
            log.info("Ended registration to user: " + chatId);

            sendMessage(chatId, "Регистрация завершена, активирована пробная подписка на месяц");
        } else {
            sendMessage(chatId, "Данный клуб уже зарегистрирован");
        }
        log.info("Sent message to: " + chatId);
    }

    private void sendMessage(Long chatId, String message) {
        telegramSender.sendMessage(chatId, message);
    }

    private void setAction(Long chatId) {
        log.info("Removing action to user: " + chatId);
        TelegramClient client = clientService.findOne(chatId);
        client.setAction("none");
        clientService.save(client);
        log.info("Removed action to user: " + chatId);
    }
}
