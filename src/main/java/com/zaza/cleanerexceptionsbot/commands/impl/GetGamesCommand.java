package com.zaza.cleanerexceptionsbot.commands.impl;

import com.zaza.cleanerexceptionsbot.commands.Command;
import com.zaza.cleanerexceptionsbot.models.mongo.Club;
import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import com.zaza.cleanerexceptionsbot.services.ClubService;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetGamesCommand implements Command<Long> {

    private final TelegramSender telegramSender;
    private final TelegramClientService clientService;
    private final ClubService clubService;

    @Override
    public void execute(Long chatId) {
        getGames(chatId);
        setAction(chatId);
    }

    private void getGames(Long chatId) {
        TelegramClient client = clientService.findOne(chatId);
        Club club = clubService.findOne(client.getClubId());

        StringBuilder msg = new StringBuilder().append("Список исключений на удаление:\n\n");

        msg.append("Steam:\n");
        club.getSteamGames().keySet().forEach(game -> msg.append(game).append("\n"));

        msg.append("\nEpic Games:\n");
        club.getEgsGames().forEach(game -> msg.append(game).append("\n"));

        msg.append("\nVK Play:\n");
        club.getVkGames().forEach(game -> msg.append(game).append("\n"));

        msg.append("\nUbisoft:\n");
        club.getUbisoftGames().forEach(game -> msg.append(game).append("\n"));

        msg.append("\nBattle Net:\n");
        club.getBattleNetGames().forEach(game -> msg.append(game).append("\n"));

        sendMessage(msg.toString(), chatId);
    }

    private void sendMessage(String message, Long chatId) {
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
