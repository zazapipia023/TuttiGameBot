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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteEgsGameAction implements Action {

    private final TelegramSender telegramSender;
    private final ClubService clubService;
    private final TelegramClientService clientService;

    @Override
    public void execute(Long chatId, String text) {
        deleteGame(chatId, text);
        setAction(chatId);
    }

    private void deleteGame(Long chatId, String game) {
        if (DefaultGames.egsGames.contains(game)) {
            log.info("Game " + game + " set as default game, can't be deleted");
            sendMessage(chatId, "Данную игру нельзя удалить");
        } else {
            TelegramClient client = clientService.findOne(chatId);
            Club club = clubService.findOne(client.getClubId());

            if (club.getEgsGames().contains(game)) {
                List<String> games = club.getEgsGames();
                games.remove(game);
                clubService.save(club);
                log.info("Deleted game " + game);
                sendMessage(chatId, "Игра удалена");
            }
            else {
                log.info("Game " + game + " doesn't exist in list");
                sendMessage(chatId, "Данной игры нет в списке");
            }
            log.info("Sent message to: " + chatId);
        }
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
