package com.zaza.cleanerexceptionsbot.actions.impl;

import com.zaza.cleanerexceptionsbot.actions.Action;
import com.zaza.cleanerexceptionsbot.models.mongo.Club;
import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import com.zaza.cleanerexceptionsbot.services.ClubService;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddSteamGameAction implements Action {

    private final TelegramSender telegramSender;
    private final ClubService clubService;
    private final TelegramClientService clientService;

    @Override
    public void execute(Long chatId, String text) {
        saveGame(chatId, text);
        setAction(chatId);
    }

    private void saveGame(Long chatId, String game) {
        TelegramClient client = clientService.findOne(chatId);
        Club club = clubService.findOne(client.getClubId());
        Map<String, String> games = club.getSteamGames();

        if (isValidString(game)) {
            String[] gameAndId = game.split(":");

            if (games.containsKey(game)) {
                log.info("Game " + game + " already exist in list");
                sendMessage(chatId, "Данная игра уже была добавлена ранее");
            } else {
                games.put(gameAndId[0], gameAndId[1]);
                club.setSteamGames(games);
                clubService.save(club);
                log.info("Saved game " + game + " to list");
                sendMessage(chatId, "Игра добавлена");
            }
        } else {
            log.info("Wrong game format for game " + game);
            sendMessage(chatId, "Неверно указан ID");
        }
        log.info("Sent message to: " + chatId);
    }

    private boolean isValidString(String game) {
        String[] gameAndId = game.split(":");
        if (gameAndId[1].matches("\\d+")) {
            return true;
        }
        return false;
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
