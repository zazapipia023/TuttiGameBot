package com.zaza.tuttifruttibot.services;

import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player findOne(Long id) {
        log.debug("Searching for player with ID: {}", id);
        Optional<Player> foundPlayer = playerRepository.findById(id);

        if (foundPlayer.isPresent()) {
            Player player = foundPlayer.get();
            log.debug("Player found - ID: {}, Name: {}, Value: {}g, Profit: {} rub",
                    player.getId(), player.getName(), player.getValue(), player.getProfit());
            return player;
        } else {
            log.info("Player with ID {} not found in database", id);
            return null;
        }
    }

    public List<Player> findAllByDescending() {
        log.info("Retrieving all players ordered by profit descending");
        List<Player> players = playerRepository.findAllByOrderByProfitDesc();

        log.info("Found {} players in database", players.size());
        if (!players.isEmpty()) {
            log.debug("Top 3 players by profit: {} ({} rub), {} ({} rub), {} ({} rub)",
                    players.get(0).getName(), players.get(0).getProfit(),
                    players.size() > 1 ? players.get(1).getName() : "N/A",
                    players.size() > 1 ? players.get(1).getProfit() : "N/A",
                    players.size() > 2 ? players.get(2).getName() : "N/A",
                    players.size() > 2 ? players.get(2).getProfit() : "N/A");
        } else {
            log.warn("No players found in database");
        }

        return players;
    }

    @Transactional
    public void save(Player player) {
        log.info("Saving player to database: {}", player.getName());
        log.debug("Player data before save - ID: {}, Value: {}g, Profit: {} rub",
                player.getId(), player.getValue(), player.getProfit());

        try {
            Player savedPlayer = playerRepository.save(player);
            log.info("Player successfully saved - ID: {}, Name: {}",
                    savedPlayer.getId(), savedPlayer.getName());
            log.debug("Player details after save - Value: {}g, Profit: {} rub",
                    savedPlayer.getValue(), savedPlayer.getProfit());
        } catch (Exception e) {
            log.error("Failed to save player: {}", player.getName(), e);
            throw new RuntimeException("Error saving player to database", e);
        }
    }

    public List<Player> findAll() {
        return  playerRepository.findAll();
    }
}
