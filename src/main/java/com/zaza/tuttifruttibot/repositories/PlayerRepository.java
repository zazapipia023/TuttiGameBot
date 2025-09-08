package com.zaza.tuttifruttibot.repositories;

import com.zaza.tuttifruttibot.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findAllByOrderByProfitDesc();

}
