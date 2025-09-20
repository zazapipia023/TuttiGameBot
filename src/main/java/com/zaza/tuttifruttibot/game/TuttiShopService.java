package com.zaza.tuttifruttibot.game;

import com.zaza.tuttifruttibot.controllers.IceShopController;
import com.zaza.tuttifruttibot.controllers.PlayerController;
import com.zaza.tuttifruttibot.models.IceShop;
import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class TuttiShopService {

    private final TelegramSender telegramSender;
    private final TuttiFruttiService tuttiFruttiService;
    private final PlayerController playerController;
    private final IceShopController iceShopController;
    private final Integer OPEN_SHOP_COST = 500000;
    private final Integer MAX_CREAM_VALUE = 5000;

    public boolean processShopBuying(Long userId) {
        Player player = playerController.findPlayer(userId);
        if (player.getProfit() < OPEN_SHOP_COST) {
            return false;
        } else {
            iceShopController.createShop(player);
            playerController.processShopBuying(player, OPEN_SHOP_COST);
            return true;
        }
    }

    @Scheduled(cron = "0 0 10 * * *")
//    @Scheduled(cron = "0 * * * * *")
    private void processCreamArrive() {
        List<IceShop> shops = iceShopController.findAllShops();

        shops.forEach(shop -> {
            shop.setValue(shop.getValue() + ThreadLocalRandom.current().nextInt(0, MAX_CREAM_VALUE));
        });

        iceShopController.saveAllShops(shops);
    }

    @Scheduled(cron = "0 0 10,12,14,16,18,20 * * *")
//    @Scheduled(cron = "0 28 * * * *")
    private void processCreamSell() {
        List<IceShop> shops = iceShopController.findAllShops();

        shops.forEach(shop -> {
            int creamToSell = ThreadLocalRandom.current().nextInt(0, shop.getValue() / 2);
            shop.setValue(shop.getValue() - creamToSell);
            shop.setProfit(shop.getProfit() + creamToSell * 3);
        });

        iceShopController.saveAllShops(shops);
    }

    public String getShopsData(Update update) {
        Player player = playerController.findPlayer(update.getMessage().getFrom().getId());
        List<IceShop> shops = iceShopController.findPlayersShops(player);

        StringBuilder shopsData = new StringBuilder()
                .append(shops.isEmpty() ? "У вас нет открытых точек" : "Ваши открытые точки:\n\n");

        shops.forEach(shop -> {
            shopsData.append(shop.getShopName()).append(" | В сейфе: ").append(shop.getProfit()).append(" руб. | На складе: ").append(shop.getValue()).append(" гр.");
        });

        return shopsData.toString();
    }
}
