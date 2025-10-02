package com.zaza.tuttifruttibot.controllers;

import com.github.javafaker.Faker;
import com.zaza.tuttifruttibot.models.IceShop;
import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.services.IceShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IceShopController {

    private final IceShopService iceShopService;
    private final Faker faker = new Faker();

    public IceShop findShop(Long id) {
        IceShop iceShop = iceShopService.findOne(id);

        if (iceShop == null) {
            log.info("Shop with ID {} not found", id);
        } else {
            log.debug("Found shop: {}", iceShop);
        }

        return iceShop;
    }

    public List<IceShop> findAllShops() {
        return iceShopService.findAllShops();
    }

    public void saveIceShop(IceShop iceShop) {
        log.info("Saving shop: {}", iceShop.getShopName());
        log.debug("Shop data before saving - Shop Owner: {}", iceShop.getPlayer().getName());
        iceShopService.save(iceShop);
    }

    public void createShop(Player player) {
        IceShop iceShop = new IceShop();
        iceShop.setPlayer(player);
        iceShop.setShopName(faker.company().name());
        iceShop.setProfit(0);
        iceShop.setValue(0);
        iceShop.setTotalCream(0);
        iceShop.setTotalProfit(0);
        iceShop.setUpgrades(new ArrayList<>());
        saveIceShop(iceShop);
    }

    public List<IceShop> findPlayersShops(Player player) {
        return iceShopService.findAllByPlayerId(player);
    }

    public void saveAllShops(List<IceShop> shops) {
        iceShopService.saveAll(shops);
    }
}
