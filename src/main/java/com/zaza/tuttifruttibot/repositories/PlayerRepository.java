package com.zaza.tuttifruttibot.repositories;

import com.zaza.tuttifruttibot.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findAllByChatId(Long chatId);

    Optional<Player> findByUserIdAndChatId(Long userId, Long chatId);

}
