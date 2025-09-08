package com.zaza.tuttifruttibot.services;

import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player findOne(Long id) {
        Optional<Player> foundPlayer = playerRepository.findById(id);
        return foundPlayer.orElse(null);
    }

    public List<Player> findAllByDescending() {
        return playerRepository.findAllByOrderByProfitDesc();
    }

    @Transactional
    public void save(Player player) {
        playerRepository.save(player);
    }

}
