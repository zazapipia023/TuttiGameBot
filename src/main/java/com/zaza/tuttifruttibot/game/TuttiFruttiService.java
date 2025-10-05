package com.zaza.tuttifruttibot.game;

import com.zaza.tuttifruttibot.controllers.PlayerController;
import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.utils.TelegramEmoji;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class TuttiFruttiService {

    private final PlayerController playerController;
    private final Map<Long, LocalDateTime> commandCooldowns = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> sellCooldowns = new ConcurrentHashMap<>();

    private static final int COOLDOWN_HOURS = 6;
    private static final int SELL_COOLDOWN_HOURS = 3;
    private static final double POSITIVE_PROBABILITY = 0.8;
    private static final double SELL_POSITIVE_PROBABILITY = 0.98;

    public String makeIceCream(Update update) {
        log.info("makeIceCream started");
        Long playerId = update.getMessage() == null ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId();
        String playerName = update.getMessage() == null ? update.getCallbackQuery().getFrom().getFirstName() : update.getMessage().getFrom().getFirstName();
        log.debug("Player info - ID: {}, Name: {}", playerId, playerName);

        if (isOnCooldown(playerId, commandCooldowns, COOLDOWN_HOURS)) {
            log.info("Player {} is on cooldown for makeIceCream", playerName);
            return formatCooldownMessage(playerName, commandCooldowns.get(playerId), COOLDOWN_HOURS);
        }

        commandCooldowns.put(playerId, LocalDateTime.now());
        log.debug("Cooldown set for player {}", playerId);

        Player player = getOrCreatePlayer(playerId, playerName);
        log.debug("Player data: {}", player);

        int value = generateRandomValue(-1500, 8000, POSITIVE_PROBABILITY);
        log.info("Generated random value: {} for player {}", value, playerName);

        int newValue = Math.max(player.getValue() + value, 0);
        log.debug("New value after calculation: {}", newValue);

        player.setValue(newValue);
        playerController.savePlayer(player);
        log.info("Player {} value updated from {} to {}", playerName, player.getValue() - value, newValue);

        return formatResultMessage(playerName, value, newValue);
    }

    public String getIceCreamValue(Update update) {
        log.info("getIceCreamValue started");
        Long playerId = update.getMessage().getFrom().getId();
        Player player = playerController.findPlayer(playerId);

        if (player == null) {
            log.warn("Player with ID {} not found for getIceCreamValue", playerId);
            return "Сначала введи команду /tutti_frutti@idrakG_bot";
        }

        log.info("Returning value for player {}: {} grams, {} rub profit",
                player.getName(), player.getValue(), player.getProfit());
        return "На твоей точке " + player.getValue() + " гр. мороженого.\n" +
                "Капитал твоей точки: " + player.getProfit() + " руб.";
    }

    public String sellIceCream(Update update) {
        log.info("sellIceCream started");
        Long playerId = update.getMessage() == null ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId();
        Player player = playerController.findPlayer(playerId);

        if (player == null) {
            log.warn("Player with ID {} not found for sellIceCream", playerId);
            return "Сначала введи команду /tutti_frutti@idrakG_bot";
        }

        log.debug("Found player: {} with value: {}", player.getName(), player.getValue());

        if (isOnCooldown(playerId, sellCooldowns, SELL_COOLDOWN_HOURS)) {
            log.info("Player {} is on sell cooldown", player.getName());
            return formatSellCooldownMessage(player.getName(), sellCooldowns.get(playerId), SELL_COOLDOWN_HOURS);
        }

        sellCooldowns.put(playerId, LocalDateTime.now());
        log.debug("Sell cooldown set for player {}", playerId);

        if (player.getValue() < 100) {
            log.info("Player {} has insufficient ice cream: {} grams", player.getName(), player.getValue());
            return player.getName() + ", у тебя слишком мало мороженого для продажи\\.\nКлиент ушел недовольный\\.";
        }

        log.info("Processing sale for player {} with {} grams available", player.getName(), player.getValue());
        return processSale(player);
    }

    public String makeTop() {
        log.info("makeTop started");
        StringBuilder sb = new StringBuilder();
        sb.append(TelegramEmoji.SHAVED_ICE.getEmojiCode());
        sb.append("*Лучшие мороженщики Tutti Frutti:*");
        sb.append(TelegramEmoji.SHAVED_ICE.getEmojiCode());
        sb.append("\n");

        var topPlayers = playerController.makeTopPlayers();
        log.info("Found {} top players", topPlayers.size());

        topPlayers.forEach(player -> {
            sb.append("\n*__").append(player.getName()).append("__ —* ").append(player.getProfit()).append(" руб\\.");
            log.debug("Added player {} to top with profit {}", player.getName(), player.getProfit());
        });

        log.info("Top list generated with {} players", topPlayers.size());
        return sb.toString();
    }

    private Player getOrCreatePlayer(Long playerId, String playerName) {
        log.debug("getOrCreatePlayer for ID: {}", playerId);
        Player player = playerController.findPlayer(playerId);

        if (player == null) {
            log.info("Creating new player with ID: {}, Name: {}", playerId, playerName);
            player = new Player(playerId, "none", playerName, 0, 0, Collections.emptyList());
        } else {
            log.debug("Existing player found: {}", player);
        }

        return player;
    }

    private boolean isOnCooldown(Long playerId, Map<Long, LocalDateTime> cooldownMap, int hours) {
        boolean onCooldown = cooldownMap.containsKey(playerId) &&
                LocalDateTime.now().isBefore(cooldownMap.get(playerId).plusHours(hours));

        log.debug("Cooldown check for player {}: {}", playerId, onCooldown ? "ON cooldown" : "NOT on cooldown");
        return onCooldown;
    }

    private String formatCooldownMessage(String playerName, LocalDateTime lastUsage, int cooldownHours) {
        long remainingTime = getRemainingTime(lastUsage, cooldownHours);
        log.debug("Formatting cooldown message for {} - {} minutes remaining", playerName, remainingTime);
        return playerName + ", слишком большая нагрузка поставщиков.\n" +
                "Следующая поставка доступна через " + remainingTime + " мин.";
    }

    private String formatSellCooldownMessage(String playerName, LocalDateTime lastUsage, int cooldownHours) {
        long remainingTime = getRemainingTime(lastUsage, cooldownHours);
        log.debug("Formatting sell cooldown message for {} - {} minutes remaining", playerName, remainingTime);
        return playerName + ", сейчас нет потока клиентов.\n" +
                "Попробуй через " + remainingTime + " мин.";
    }

    private long getRemainingTime(LocalDateTime lastUsage, int cooldownHours) {
        long minutes = Duration.between(LocalDateTime.now(), lastUsage.plusHours(cooldownHours)).toMinutes();
        log.trace("Calculated remaining time: {} minutes", minutes);
        return minutes;
    }

    private int generateRandomValue(int min, int max, double positiveProbability) {
        double random = ThreadLocalRandom.current().nextDouble();
        int value;

        if (random < positiveProbability) {
            value = ThreadLocalRandom.current().nextInt(0, max + 1);
            log.trace("Generated positive value: {} (random: {})", value, random);
        } else {
            value = ThreadLocalRandom.current().nextInt(min, 0);
            log.trace("Generated negative value: {} (random: {})", value, random);
        }

        return value;
    }

    private String formatResultMessage(String playerName, int value, int newValue) {
        log.debug("Formatting result message for {} - value: {}, newValue: {}", playerName, value, newValue);
        return playerName + ", " +
                (value < 0 ? "тебе пришлось угостить Ахмеда\\. " + Math.abs(value) + " гр\\. мороженого он съел\\.\n" :
                        "тебе привезли " + value + " гр\\. мороженого\\.\n") +
                "Теперь у тебя " + newValue + " гр\\. мороженого на точке\\.";
    }

    private String processSale(Player player) {
        log.info("Processing sale for player: {}", player.getName());
        int min = 100;
        int max = player.getValue();
        int value = ThreadLocalRandom.current().nextInt(min, max / 2);
        log.debug("Sale amount determined: {} grams (range: {}-{})", value, min, max);

        double random = ThreadLocalRandom.current().nextDouble();
        log.debug("Sale success check - random: {}, threshold: {}", random, SELL_POSITIVE_PROBABILITY);

        if (random < SELL_POSITIVE_PROBABILITY) {
            log.info("Successful sale for player {}: {} grams", player.getName(), value);
            return processSuccessfulSale(player, value);
        } else {
            log.info("Failed sale for player {}: {} grams stolen", player.getName(), value);
            return processFailedSale(player, value);
        }
    }

    private String processSuccessfulSale(Player player, int value) {
        int oldValue = player.getValue();
        int oldProfit = player.getProfit();
        int profitGained = value * 3;

        player.setValue(oldValue - value);
        player.setProfit(oldProfit + profitGained);
        playerController.savePlayer(player);

        log.info("Sale completed successfully - Player: {}, Sold: {}g, Profit: +{} rub, New total: {}g, {} rub",
                player.getName(), value, profitGained, player.getValue(), player.getProfit());

        return "[" + player.getName() + "](tg://user?id=" + player.getId() + "), ты продал " + value + " гр\\. мороженого и получил за это " + profitGained + " руб\\.";
    }

    private String processFailedSale(Player player, int value) {
        int oldValue = player.getValue();
        int oldProfit = player.getProfit();
        int loss = value * 3;

        player.setValue(oldValue - value);
        player.setProfit(Math.max(oldProfit - loss, 0));
        playerController.savePlayer(player);

        log.info("Sale failed - Player: {}, Lost: {}g, Penalty: -{} rub, New total: {}g, {} rub",
                player.getName(), value, loss, player.getValue(), player.getProfit());

        return "[" + player.getName() + "](tg://user?id=" + player.getId() + "), ты засмотрелся на жопу студентки, и у тебя спиздили " + value + " гр\\. мороженого\\.\n" +
                "Тебе пришлось заплатить " + loss + " руб\\.";
    }

    public String getPlayerData(Long userId, String name) {
        Player player = playerController.findPlayer(userId);
        if (player == null) {
            player = new Player();
            player.setId(userId);
            player.setName(name);
            player.setProfit(0);
            player.setValue(0);
            player.setAction("none");
            playerController.savePlayer(player);
        }

        StringBuilder sb = new StringBuilder("У тебя:\n")
                .append("Мороженое: ").append(player.getValue()).append(" гр\\.\n")
                .append("Баланс: ").append(player.getProfit()).append(" руб\\.");

        return sb.toString();
    }
}
