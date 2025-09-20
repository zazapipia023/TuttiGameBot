package com.zaza.tuttifruttibot.repositories;

import com.zaza.tuttifruttibot.models.IceShop;
import com.zaza.tuttifruttibot.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IceShopRepository extends JpaRepository<IceShop, Long> {
    List<IceShop> findAllByPlayer(Player player);
}
