package com.zaza.tuttifruttibot.controllers;

import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.services.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    public Player findPlayer(Long id) {
        return playerService.findOne(id);
    }

    public List<Player> makeTopPlayers() {
        return playerService.findAllByDescending();
    }

    public void savePlayer(Player player) {
        playerService.save(player);
    }

}
