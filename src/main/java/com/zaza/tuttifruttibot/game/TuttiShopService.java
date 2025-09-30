package com.zaza.tuttifruttibot.game;

import com.zaza.tuttifruttibot.controllers.IceShopController;
import com.zaza.tuttifruttibot.controllers.PlayerController;
import com.zaza.tuttifruttibot.models.IceShop;
import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import com.zaza.tuttifruttibot.upgrades.HardwareEquipment;
import com.zaza.tuttifruttibot.upgrades.IceCreamTypes;
import com.zaza.tuttifruttibot.upgrades.Toppings;
import com.zaza.tuttifruttibot.upgrades.UpgradeType;
import com.zaza.tuttifruttibot.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
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

    private static final int OPEN_SHOP_COST = 500000;
    private static final int MAX_CREAM_VALUE = 5000;
    private static final int TOPPING_COST = 15000;
    private static final int ICE_CREAM_COST = 65000;
    private static final int HARDWARE_COST = 150000;
    private static final int CREAM_SELL_MULTIPLIER = 3;

    private static final String NO_SHOPS_MESSAGE = "У вас нет открытых точек";
    private static final String NO_MONEY_ON_SHOP_MESSAGE = "На точке недостаточно денег для покупки ";
    private static final String ALL_SHOPS_HAVE_UPGRADE_MESSAGE = "На всех твоих точках есть этот ";
    private static final String ALREADY_HAVE_UPGRADE_MESSAGE = "У тебя уже есть данный ";

    public boolean processShopBuying(Long userId) {
        Player player = playerController.findPlayer(userId);
        if (player.getProfit() < OPEN_SHOP_COST) {
            return false;
        }

        iceShopController.createShop(player);
        playerController.processShopBuying(player, OPEN_SHOP_COST);
        return true;
    }

    @Scheduled(cron = "0 0 10 * * *")
    private void processCreamArrive() {
        log.info("Processing Cream Arrive");
        List<IceShop> shops = iceShopController.findAllShops();

        shops.forEach(shop -> {
            int cream = ThreadLocalRandom.current().nextInt(0, MAX_CREAM_VALUE);
            shop.setValue(shop.getValue() + cream);
            shop.setTotalCream(shop.getTotalCream() + cream);
            log.info("Cream Arrive - {}", shop.getValue());
        });

        log.info("Cream Arrive Completed");
        iceShopController.saveAllShops(shops);
    }

    @Scheduled(cron = "0 0 10,12,14,16,18,20 * * *")
    private void processCreamSell() {
        log.info("Processing Cream Sell from shops");
        List<IceShop> shops = iceShopController.findAllShops();

        shops.forEach(this::processCreamSellForShop);

        log.info("Cream Sell Completed");
        iceShopController.saveAllShops(shops);
    }

    private void processCreamSellForShop(IceShop shop) {
        log.info("Cream Sell from shop {}", shop.getShopName());

        double saleIndex = calculateSaleIndex(shop.getUpgrades());
        int creamToSell = ThreadLocalRandom.current().nextInt(0, shop.getValue() / 2);

        log.info("saleIndex: {} | cream value: {}", saleIndex, creamToSell);
        creamToSell = (int) (creamToSell * saleIndex);
        log.info("final cream value: {}", creamToSell);

        int actualCreamSold = Math.min(creamToSell, shop.getValue());
        int profitFromSale = actualCreamSold * CREAM_SELL_MULTIPLIER;

        shop.setValue(shop.getValue() - actualCreamSold);
        shop.setProfit(shop.getProfit() + profitFromSale);
        shop.setTotalCream(shop.getTotalCream() + actualCreamSold);

        log.info("Sold {} ice cream for {} profit", actualCreamSold, profitFromSale);
    }

    public String getShopsData(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Player player = playerController.findPlayer(userId);
        List<IceShop> shops = iceShopController.findPlayersShops(player);

        if (shops.isEmpty()) {
            return NO_SHOPS_MESSAGE;
        }

        StringBuilder shopsData = new StringBuilder("Ваши открытые точки:\n\n");
        shops.forEach(shop ->
                shopsData.append(shop.getShopName())
                        .append(" | В сейфе: ")
                        .append(shop.getProfit())
                        .append(" руб. | На складе: ")
                        .append(shop.getValue())
                        .append(" гр.\n")
        );

        return shopsData.toString();
    }

    public void processToppingUpgrade(String topping, Long chatId, Long userId, String callbackId) {
        processUpgrade(topping, chatId, userId, callbackId, UpgradeType.TOPPING);
    }

    public void processIceCreamUpgrade(String iceCream, Long chatId, Long userId, String callbackId) {
        processUpgrade(iceCream, chatId, userId, callbackId, UpgradeType.ICE_CREAM);
    }

    public void processHardwareUpgrade(String hardware, Long chatId, Long userId, String callbackId) {
        processUpgrade(hardware, chatId, userId, callbackId, UpgradeType.HARDWARE);
    }

    private void processUpgrade(String upgrade, Long chatId, Long userId, String callbackId, UpgradeType upgradeType) {
        Player player = playerController.findPlayer(userId);
        List<IceShop> iceShops = iceShopController.findPlayersShops(player);

        Optional<IceShop> availableShop = findShopWithoutUpgrade(iceShops, upgrade);

        if (availableShop.isEmpty()) {
            String message = ALL_SHOPS_HAVE_UPGRADE_MESSAGE + upgradeType.getRussianName();
            telegramSender.sendAlertResponse(callbackId, message);
            return;
        }

        IceShop iceShop = availableShop.get();
        int upgradeCost = getUpgradeCost(upgradeType);

        if (!validateUpgradePurchase(iceShop, upgrade, upgradeCost, callbackId, upgradeType)) {
            return;
        }

        applyUpgrade(iceShop, upgrade, upgradeCost);
        iceShopController.saveIceShop(iceShop);

        String successMessage = createSuccessMessage(upgrade, upgradeType);
        telegramSender.sendMessage(chatId, successMessage);
    }

    private Optional<IceShop> findShopWithoutUpgrade(List<IceShop> iceShops, String upgrade) {
        return iceShops.stream()
                .filter(shop -> !shop.getUpgrades().contains(upgrade))
                .findFirst();
    }

    private boolean validateUpgradePurchase(IceShop iceShop, String upgrade, int cost,
                                            String callbackId, UpgradeType upgradeType) {
        if (iceShop.getProfit() < cost) {
            telegramSender.sendAlertResponse(callbackId, NO_MONEY_ON_SHOP_MESSAGE + upgradeType.getRussianName());
            return false;
        }

        if (iceShop.getUpgrades().contains(upgrade)) {
            telegramSender.sendAlertResponse(callbackId, ALREADY_HAVE_UPGRADE_MESSAGE + upgradeType.getRussianName());
            return false;
        }

        return true;
    }

    private void applyUpgrade(IceShop iceShop, String upgrade, int cost) {
        iceShop.setProfit(iceShop.getProfit() - cost);
        List<String> upgrades = iceShop.getUpgrades();
        upgrades.add(upgrade);
        iceShop.setUpgrades(upgrades);
    }

    private int getUpgradeCost(UpgradeType upgradeType) {
        return switch (upgradeType) {
            case TOPPING -> TOPPING_COST;
            case ICE_CREAM -> ICE_CREAM_COST;
            case HARDWARE -> HARDWARE_COST;
        };
    }

    private String createSuccessMessage(String upgrade, UpgradeType upgradeType) {
        String upgradeName = switch (upgradeType) {
            case TOPPING -> Toppings.getTranslate(upgrade);
            case ICE_CREAM -> IceCreamTypes.getTranslate(upgrade);
            case HARDWARE -> HardwareEquipment.getTranslate(upgrade);
        };

        return "Поздравляю! Теперь на твоей точке есть " + upgradeType.getRussianName() + ": " + upgradeName;
    }

    private double calculateSaleIndex(List<String> upgrades) {
        if (upgrades.isEmpty()) {
            log.info("no upgrades in list, returning 1.");
            return 1;
        }

        try {
            String randomUpgrade = upgrades.get(ThreadLocalRandom.current().nextInt(upgrades.size()));
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
            telegramSender.editMessageWithMarkup(chatId, messageId, "Нет открытых точек",
                    KeyboardUtils.createBackKeyboardMarkup());
            return;
        }

        AtomicInteger encashmentMoney = new AtomicInteger();
        iceShops.forEach(shop -> {
            encashmentMoney.addAndGet(shop.getProfit());
            shop.setProfit(0);
        });

        player.setProfit(player.getProfit() + encashmentMoney.get());
        playerController.savePlayer(player);
        iceShopController.saveAllShops(iceShops);

        String text = encashmentMoney.get() == 0
                ? "На точках нет денег для инкассации."
                : "На твой баланс инкассировано " + encashmentMoney.get() + " руб.";

        telegramSender.editMessageWithMarkup(chatId, messageId, text,
                KeyboardUtils.createBackKeyboardMarkup());
    }

    public String getShopStats(Long userId) {
        Player player = playerController.findPlayer(userId);
        List<IceShop> iceShops = iceShopController.findPlayersShops(player);

        if (iceShops.isEmpty()) {
            return NO_SHOPS_MESSAGE;
        }

        StringBuilder sb = new StringBuilder("Статистика ваших точек:\n\n");
        iceShops.forEach(shop ->
                sb.append(shop.getShopName()).append(":\n")
                        .append("Общий доход (руб.): ").append(shop.getTotalProfit()).append("\n")
                        .append("Всего мороженого продано (гр.): ").append(shop.getTotalCream()).append("\n")
                        .append("Количество улучшений: ").append(shop.getUpgrades().size()).append("\n\n")
        );

        return sb.toString();
    }
}
