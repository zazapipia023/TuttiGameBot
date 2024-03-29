package com.zaza.cleanerexceptionsbot.services;

import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.repositories.postgres.TelegramClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TelegramClientService {

    private final TelegramClientRepository clientRepository;

    public TelegramClient findOne(Long chatId) {
        Optional<TelegramClient> foundClient = clientRepository.findById(chatId);
        return foundClient.orElse(null);
    }

    public TelegramClient findByClubId(String clubId) {
        Optional<TelegramClient> foundClient = clientRepository.findByClubId(clubId);
        return foundClient.orElse(null);
    }

    @Transactional
    public void save(TelegramClient client) {
        clientRepository.save(client);
    }

}
