package com.zaza.cleanerexceptionsbot.repositories.postgres;

import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramClientRepository extends JpaRepository<TelegramClient, Long> {

    Optional<TelegramClient> findByClubId(String clubId);

}
