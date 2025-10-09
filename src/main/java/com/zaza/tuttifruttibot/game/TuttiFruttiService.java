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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class TuttiFruttiService {

    private final PlayerController playerController;
    private final Map<Long, Map<Long, LocalDateTime>> commandCooldowns = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, LocalDateTime>> sellCooldowns = new ConcurrentHashMap<>();

    private static final int COOLDOWN_HOURS = 6;
    private static final int SELL_COOLDOWN_HOURS = 3;
    private static final double POSITIVE_PROBABILITY = 0.8;
    private static final double SELL_POSITIVE_PROBABILITY = 0.98;

    public String makeIceCream(Update update) {
        log.info("makeIceCream started");
        Long playerId = update.getMessage() == null ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId();
        Long chatId = update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        String playerName = update.getMessage() == null ? update.getCallbackQuery().getFrom().getFirstName() : update.getMessage().getFrom().getFirstName();
        log.debug("Player info - ID: {}, Name: {}", playerId, playerName);

        if (isOnCooldown(chatId, playerId, commandCooldowns, COOLDOWN_HOURS)) {
            log.info("Player {} is on cooldown for makeIceCream", playerName);
            return formatCooldownMessage(playerName, commandCooldowns.get(chatId).get(playerId));
        }
        Map<Long, LocalDateTime> chatCooldowns = commandCooldowns.getOrDefault(chatId, new HashMap<>());
        chatCooldowns.put(playerId, LocalDateTime.now());
        commandCooldowns.put(chatId, chatCooldowns);
        log.debug("Cooldown set for player {}", playerId);

        Player player = getOrCreatePlayer(playerId, chatId, playerName);
        log.debug("Player data: {}", player);

        int value = generateRandomValue(-1500, 8000, POSITIVE_PROBABILITY);
        log.info("Generated random value: {} for player {}", value, playerName);

        int newValue = Math.max(player.getValue() + value, 0);
        log.debug("New value after calculation: {}", newValue);

        if (value < 0) {
            List<Player> players = playerController.findAllByChat(chatId);
            boolean isRemoved = players.remove(player);
            log.info("Is Removed player: {}", isRemoved);
            Player stealPlayer = players.get(ThreadLocalRandom.current().nextInt(players.size() - 1));
            log.info("Steal player name: {}", stealPlayer.getName());
            stealPlayer.setValue(stealPlayer.getValue() + Math.abs(value));
            player.setValue(newValue);
            playerController.savePlayer(player);
            playerController.savePlayer(stealPlayer);
            log.info("Player {} value updated from {} to {}", playerName, player.getValue() - value, newValue);
            log.info("Steal Player {} value updated from {} to {}", stealPlayer.getName(), stealPlayer.getValue() - Math.abs(value), stealPlayer.getValue());
            return formatStealResultMessage(playerName, playerId, value, newValue, stealPlayer);
        } else {
            player.setValue(newValue);
            playerController.savePlayer(player);
            log.info("Player {} value updated from {} to {}", playerName, player.getValue() - value, newValue);
            return formatResultMessage(playerName, playerId, value, newValue);
        }
    }

    public String sellIceCream(Update update) {
        log.info("sellIceCream started");
        Long playerId = update.getMessage() == null ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId();
        Long chatId = update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        Player player = playerController.findPlayer(playerId, chatId);

        if (player == null) {
            log.warn("Player with ID {} not found for sellIceCream", playerId);
            return "Сначала нужно произвести поставку";
        }

        log.debug("Found player: {} with value: {}", player.getName(), player.getValue());

        if (isOnCooldown(chatId, playerId, sellCooldowns, SELL_COOLDOWN_HOURS)) {
            log.info("Player {} is on sell cooldown", player.getName());
            return formatSellCooldownMessage(player.getName(), sellCooldowns.get(chatId).get(playerId));
        }

        Map<Long, LocalDateTime> chatCooldowns = sellCooldowns.getOrDefault(chatId, new HashMap<>());
        chatCooldowns.put(playerId, LocalDateTime.now());
        sellCooldowns.put(chatId, chatCooldowns);
        log.debug("Sell cooldown set for player {}", playerId);

        if (player.getValue() < 100) {
            log.info("Player {} has insufficient ice cream: {} grams", player.getName(), player.getValue());
            return player.getName() + ", у тебя слишком мало мороженого для продажи.\nКлиент ушел недовольный.";
        }

        log.info("Processing sale for player {} with {} grams available", player.getName(), player.getValue());
        return processSale(player);
    }

    public String makeTop(Long chatId) {
        log.info("makeTop started");
        StringBuilder sb = new StringBuilder();
        sb.append(TelegramEmoji.SHAVED_ICE.getEmojiCode());
        sb.append("*Лучшие мороженщики Tutti Frutti:*");
        sb.append(TelegramEmoji.SHAVED_ICE.getEmojiCode());
        sb.append("\n");

        var topPlayers = playerController.makeTopPlayers(chatId);
        if (topPlayers.size() > 10) {
            topPlayers = topPlayers.subList(0, 10);
        }

        log.info("Found {} top players", topPlayers.size());

        topPlayers.forEach(player -> {
            sb.append("\n*__").append(player.getName()).append("__ —* ").append(player.getProfit()).append(" руб.");
            log.debug("Added player {} to top with profit {}", player.getName(), player.getProfit());
        });

        log.info("Top list generated with {} players", topPlayers.size());
        return sb.toString();
    }

    private Player getOrCreatePlayer(Long playerId, Long chatId, String playerName) {
        log.debug("getOrCreatePlayer for ID: {}", playerId);
        Player player = playerController.findPlayer(playerId, chatId);

        if (player == null) {
            log.info("Creating new player with ID: {}, Name: {}", playerId, playerName);
            player = new Player(playerId, chatId, "none", playerName, 0, 0, Collections.emptyList());
        } else {
            log.debug("Existing player found: {}", player);
        }

        return player;
    }

    private boolean isOnCooldown(Long chatId, Long userId, Map<Long, Map<Long, LocalDateTime>> cooldownMap, int hours) {
        boolean onCooldown = cooldownMap.containsKey(chatId) && cooldownMap.get(chatId).containsKey(userId)
                && LocalDateTime.now().isBefore(cooldownMap.get(chatId).get(userId).plusHours(hours));

        log.debug("Cooldown check for player {}: {}", chatId, onCooldown ? "ON cooldown" : "NOT on cooldown");
        return onCooldown;
    }

    private String formatCooldownMessage(String playerName, LocalDateTime lastUsage) {
        long remainingTime = getRemainingTime(lastUsage, TuttiFruttiService.COOLDOWN_HOURS);
        log.debug("Formatting cooldown message for {} - {} minutes remaining", playerName, remainingTime);
        return "Слишком большая нагрузка поставщиков.\n" +
                "Следующая поставка доступна через " + remainingTime + " мин.";
    }

    private String formatSellCooldownMessage(String playerName, LocalDateTime lastUsage) {
        long remainingTime = getRemainingTime(lastUsage, TuttiFruttiService.SELL_COOLDOWN_HOURS);
        log.debug("Formatting sell cooldown message for {} - {} minutes remaining", playerName, remainingTime);
        return "Сейчас нет потока клиентов.\n" +
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

    private String formatResultMessage(String playerName, Long userId, int value, int newValue) {
        log.debug("Formatting result message for {} - value: {}, newValue: {}", playerName, value, newValue);
        return "[" + playerName + "](tg://user?id=" + userId + "), " +
                        "тебе привезли " + value + " гр. мороженого.\n" +
                "Теперь у тебя " + newValue + " гр. мороженого на точке.";
    }

    private String formatStealResultMessage(String playerName, Long userId, int value, int newValue, Player stealPlayer) {
        log.debug("Formatting result message for {} - value: {}, newValue: {}", playerName, value, newValue);
        return "[" + playerName + "](tg://user?id=" + userId + "), " +
                (value < 0 ? "тебе пришлось угостить [" + stealPlayer.getName() + "]" + "(tg://user?id=" + stealPlayer.getUserId() + "). "
                        + Math.abs(value) + " гр. мороженого он забрал себе.\n" :
                        "тебе привезли " + value + " гр. мороженого.\n") +
                "Теперь у тебя " + newValue + " гр. мороженого на точке.";
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

        return "[" + player.getName() + "](tg://user?id=" + player.getUserId() + "), ты продал " + value + " гр. мороженого и получил за это " + profitGained + " руб.";
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

        return "[" + player.getName() + "](tg://user?id=" + player.getId() + "), ты засмотрелся на жопу студентки, и у тебя спиздили " + value + " гр. мороженого.\n" +
                "Тебе пришлось заплатить " + loss + " руб.";
    }

    public String getPlayerData(Long userId, Long chatId, String name) {
        Player player = playerController.findPlayer(userId, chatId);
        if (player == null) {
            player = new Player();
            player.setUserId(userId);
            player.setChatId(chatId);
            player.setName(name);
            player.setProfit(0);
            player.setValue(0);
            player.setAction("none");
            playerController.savePlayer(player);
        }

        StringBuilder sb = new StringBuilder()
                .append(TelegramEmoji.ICE_CREAM.getEmojiCode())
                .append("*Твоя статистика*")
                .append(TelegramEmoji.ICE_CREAM.getEmojiCode())
                .append("\n\n")
                .append("Мороженое: ").append(player.getValue()).append(" гр.\n")
                .append("Баланс: ").append(player.getProfit()).append(" руб.");

        return sb.toString();
    }
}
