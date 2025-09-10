package com.zaza.tuttifruttibot.controllers;

import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.services.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    public Player findPlayer(Long id) {
        log.debug("Finding player by ID: {}", id);
        Player player = playerService.findOne(id);

        if (player == null) {
            log.info("Player with ID {} not found", id);
        } else {
            log.debug("Found player: {}", player);
        }

        return player;
    }

    public List<Player> makeTopPlayers() {
        log.info("Creating top players list");
        List<Player> topPlayers = playerService.findAllByDescending();

        log.info("Top players list generated with {} players", topPlayers.size());
        if (topPlayers.isEmpty()) {
            log.warn("Top players list is empty");
        } else {
            log.debug("Top player: {} with profit: {} rub",
                    topPlayers.get(0).getName(), topPlayers.get(0).getProfit());
        }

        return topPlayers;
    }

    public void savePlayer(Player player) {
        log.info("Saving player: {}", player.getName());
        log.debug("Player details before save - ID: {}, Value: {}g, Profit: {} rub",
                player.getId(), player.getValue(), player.getProfit());

        playerService.save(player);
        log.info("Player {} saved successfully", player.getName());
    }

}
