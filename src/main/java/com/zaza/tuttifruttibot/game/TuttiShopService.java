package com.zaza.tuttifruttibot.game;

import com.zaza.tuttifruttibot.controllers.IceShopController;
import com.zaza.tuttifruttibot.controllers.PlayerController;
import com.zaza.tuttifruttibot.models.IceShop;
import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import com.zaza.tuttifruttibot.upgrades.HardwareEquipment;
import com.zaza.tuttifruttibot.upgrades.IceCreamTypes;
import com.zaza.tuttifruttibot.upgrades.Toppings;
import com.zaza.tuttifruttibot.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final Integer TOPPING_COST = 15000;
    private final Integer ICE_CREAM_COST = 65000;
    private final Integer HARDWARE_COST = 150000;

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
    private void processCreamArrive() {
        log.info("Processing Cream Arrive");
        List<IceShop> shops = iceShopController.findAllShops();

        shops.forEach(shop -> {
            shop.setValue(shop.getValue() + ThreadLocalRandom.current().nextInt(0, MAX_CREAM_VALUE));
            log.info("Cream Arrive - {}", shop.getValue());
        });

        log.info("Cream Arrive Completed");
        iceShopController.saveAllShops(shops);
    }

    @Scheduled(cron = "0 0 10,12,14,16,18,20 * * *")
    private void processCreamSell() {
        log.info("Processing Cream Sell from shops");
        List<IceShop> shops = iceShopController.findAllShops();

        shops.forEach(shop -> {
            log.info("Cream Sell from shop {}", shop.getShopName());
            double saleIndex = saleIndex(shop.getUpgrades());
            int creamToSell = ThreadLocalRandom.current().nextInt(0, shop.getValue() / 2 );
            log.info("saleIndex: {} | cream value: {}", saleIndex, creamToSell);
            creamToSell = (int) (creamToSell * saleIndex);
            log.info("final cream value: {}", creamToSell);
            if (creamToSell > shop.getValue()) {
                log.info("Selling all ice cream: {}", shop.getValue());
                shop.setProfit(shop.getProfit() + shop.getValue() * 3);
                shop.setValue(0);
            } else {
                log.info("Selling ice cream");
                shop.setValue(shop.getValue() - creamToSell);
                shop.setProfit(shop.getProfit() + creamToSell * 3);
            }
        });

        log.info("Cream Sell Completed");
        iceShopController.saveAllShops(shops);
    }

    public String getShopsData(Update update) {
        Player player = playerController.findPlayer(update.getMessage().getFrom().getId());
        List<IceShop> shops = iceShopController.findPlayersShops(player);

        StringBuilder shopsData = new StringBuilder()
                .append(shops.isEmpty() ? "У вас нет открытых точек" : "Ваши открытые точки:\n\n");

        shops.forEach(shop -> {
            shopsData.append(shop.getShopName()).append(" | В сейфе: ").append(shop.getProfit()).append(" руб. | На складе: ").append(shop.getValue()).append(" гр.\n");
        });

        return shopsData.toString();
    }

    public void processToppingUpgrade(String topping, Long chatId, Long userId, String callbackId) {
        Player player = playerController.findPlayer(userId);
        List<IceShop> iceShops = iceShopController.findPlayersShops(player);
        IceShop iceShop = null;
        if (iceShops.isEmpty()) {
            telegramSender.sendAlertResponse(callbackId, "У тебя еще нет открытой точки!");
            return;
        } else {
            for (int i = 0; i < iceShops.size(); i++) {
                if (!iceShops.get(i).getUpgrades().contains(topping)) {
                    iceShop = iceShops.get(i);
                    break;
                }
            }
            if (iceShop == null) {
                telegramSender.sendAlertResponse(callbackId, "На всех твоих точках есть этот топпинг!");
                return;
            }
        }

        if (iceShop.getProfit() < TOPPING_COST) {
            telegramSender.sendAlertResponse(callbackId, "На точке недостаточно денег для покупки топпинга.");
            return;
        } else {
            iceShop.setProfit(iceShop.getProfit() - TOPPING_COST);
            List<String> upgrades = iceShop.getUpgrades();
            if (upgrades.contains(topping)) {
                telegramSender.sendAlertResponse(callbackId, "У тебя уже есть данный топпинг!");
                return;
            }
            upgrades.add(topping);
            iceShop.setUpgrades(upgrades);
        }
        iceShopController.saveIceShop(iceShop);
        telegramSender.sendMessage(chatId, "Поздравляю! Теперь на твоей точке есть топпинг: " + Toppings.getTranslate(topping));
    }

    public void processIceCreamUpgrade(String iceCream, Long chatId, Long userId, String callbackId) {
        Player player = playerController.findPlayer(userId);
        List<IceShop> iceShops = iceShopController.findPlayersShops(player);
        IceShop iceShop = null;
        if (iceShops.isEmpty()) {
            telegramSender.sendAlertResponse(callbackId, "У тебя еще нет открытой точки!");
            return;
        } else {
            for (int i = 0; i < iceShops.size(); i++) {
                if (!iceShops.get(i).getUpgrades().contains(iceCream)) {
                    iceShop = iceShops.get(i);
                    break;
                }
            }
            if (iceShop == null) {
                telegramSender.sendAlertResponse(callbackId, "На всех твоих точках есть этот вкус мороженого!");
                return;
            }
        }

        if (iceShop.getProfit() < ICE_CREAM_COST) {
            telegramSender.sendAlertResponse(callbackId, "На точке недостаточно денег для покупки мороженого.");
            return;
        } else {
            iceShop.setProfit(iceShop.getProfit() - TOPPING_COST);
            List<String> upgrades = iceShop.getUpgrades();
            if (upgrades.contains(iceCream)) {
                telegramSender.sendAlertResponse(callbackId, "У тебя уже есть данный вкус!");
                return;
            }
            upgrades.add(iceCream);
            iceShop.setUpgrades(upgrades);
        }
        iceShopController.saveIceShop(iceShop);
        telegramSender.sendMessage(chatId, "Поздравляю! Теперь на твоей точке есть мороженое: " + IceCreamTypes.getTranslate(iceCream));
    }

    public void processHardwareUpgrade(String hardware, Long chatId, Long userId, String callbackId) {
        Player player = playerController.findPlayer(userId);
        List<IceShop> iceShops = iceShopController.findPlayersShops(player);
        IceShop iceShop = null;
        if (iceShops.isEmpty()) {
            telegramSender.sendAlertResponse(callbackId, "У тебя еще нет открытой точки!");
            return;
        } else {
            for (int i = 0; i < iceShops.size(); i++) {
                if (!iceShops.get(i).getUpgrades().contains(hardware)) {
                    iceShop = iceShops.get(i);
                    break;
                }
            }
            if (iceShop == null) {
                telegramSender.sendAlertResponse(callbackId, "На всех твоих точках есть эта фурнитура!");
                return;
            }
        }

        if (iceShop.getProfit() < HARDWARE_COST) {
            telegramSender.sendAlertResponse(callbackId, "На точке недостаточно денег для покупки фурнитуры.");
            return;
        } else {
            iceShop.setProfit(iceShop.getProfit() - TOPPING_COST);
            List<String> upgrades = iceShop.getUpgrades();
            if (upgrades.contains(hardware)) {
                telegramSender.sendAlertResponse(callbackId, "У тебя уже есть эта фурнитура!");
                return;
            }
            upgrades.add(hardware);
            iceShop.setUpgrades(upgrades);
        }
        iceShopController.saveIceShop(iceShop);
        telegramSender.sendMessage(chatId, "Поздравляю! Теперь на твоей точке есть фурнитура: " + HardwareEquipment.getTranslate(hardware));
    }

    private double saleIndex(List<String> upgrades) {
        try {
            String randomUpgrade = upgrades.get(ThreadLocalRandom.current().nextInt(0, upgrades.size() - 1));
            log.info("random upgrade for saleIndex: {}", randomUpgrade);
            if (Toppings.isTopping(randomUpgrade)) {
                return Toppings.getIndex(randomUpgrade);
            }
            if (HardwareEquipment.isHardwareEquipment(randomUpgrade)) {
                return HardwareEquipment.getIndex(randomUpgrade);
            }
            if (IceCreamTypes.isIceCreamType(randomUpgrade)) {
                return IceCreamTypes.getIndex(randomUpgrade);
            }
            log.debug("random upgrade not in enum: {}", randomUpgrade);
        } catch (IndexOutOfBoundsException e) {
            log.info("no upgrades in list, returning 1.");
            return 1;
        }
        return 1;
    }

    public void processEncashment(Long chatId, Integer messageId, Long userId) {
        Player player = playerController.findPlayer(userId);
        List<IceShop> iceShops = iceShopController.findPlayersShops(player);
        if (iceShops.isEmpty()) {
            telegramSender.editMessageWithMarkup(chatId, messageId, "Нет открытых точек", KeyboardUtils.createBackKeyboardMarkup());
            return;
        }
        AtomicInteger encashmentMoney = new AtomicInteger();

        iceShops.forEach(iceShop -> {
            encashmentMoney.addAndGet(iceShop.getProfit());
            iceShop.setProfit(0);
        });

        player.setProfit(player.getProfit() + encashmentMoney.get());
        playerController.savePlayer(player);
        iceShopController.saveAllShops(iceShops);

        String text;
        if (encashmentMoney.get() == 0) {
            text = "На точках нет денег для инкассации.";
        } else {
            text = "На твой баланс инкассировано " + encashmentMoney.get() + " руб.";
        }


        telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createBackKeyboardMarkup());
    }
}
