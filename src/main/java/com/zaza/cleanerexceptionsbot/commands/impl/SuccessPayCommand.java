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

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessPayCommand implements Command<Long> {

    private final TelegramSender telegramSender;
    private final ClubService clubService;
    private final TelegramClientService clientService;

    @Override
    public void execute(Long chatId) {
        TelegramClient client = clientService.findOne(chatId);
        Club club = clubService.findOne(client.getClubId());
        club.setSubDate(LocalDateTime.now());
        clubService.save(club);

        telegramSender.sendMessage(chatId, "Оплата подписки успешно проведена, подписка продлена на месяц");
    }
}
