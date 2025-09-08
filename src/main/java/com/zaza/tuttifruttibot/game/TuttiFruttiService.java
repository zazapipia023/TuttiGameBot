package com.zaza.tuttifruttibot.game;

import com.zaza.tuttifruttibot.controllers.PlayerController;
import com.zaza.tuttifruttibot.models.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class TuttiFruttiService {

    private final PlayerController playerController;
    private final Map<Long, LocalDateTime> commandCooldowns = new ConcurrentHashMap<>();
    private final Map<Long, LocalDateTime> sellCooldowns = new ConcurrentHashMap<>();

    private static final int COOLDOWN_HOURS = 5;
    private static final int SELL_COOLDOWN_HOURS = 1;
    private static final double POSITIVE_PROBABILITY = 0.8;
    private static final double SELL_POSITIVE_PROBABILITY = 0.98;

    public String makeIceCream(Update update) {
        Long playerId = update.getMessage().getFrom().getId();
        String playerName = update.getMessage().getFrom().getFirstName();

        if (isOnCooldown(playerId, commandCooldowns, COOLDOWN_HOURS)) {
            return formatCooldownMessage(playerName, commandCooldowns.get(playerId), COOLDOWN_HOURS);
        }

        commandCooldowns.put(playerId, LocalDateTime.now());

        Player player = getOrCreatePlayer(playerId, playerName);
        int value = generateRandomValue(-500, 1000, POSITIVE_PROBABILITY);
        int newValue = Math.max(player.getValue() + value, 0);

        player.setValue(newValue);
        playerController.savePlayer(player);

        return formatResultMessage(playerName, value, newValue);
    }

    public String getIceCreamValue(Update update) {
        Player player = playerController.findPlayer(update.getMessage().getFrom().getId());
        return "На твоей точке " + player.getValue() + " гр. мороженого.\n" +
                "Капитал твоей точки: " + player.getProfit() + " руб.";
    }

    public String sellIceCream(Update update) {
        Long playerId = update.getMessage().getFrom().getId();
        Player player = playerController.findPlayer(playerId);
        if (player == null) {
            return "Сначала введи команду /tutti_frutti";
        }

        if (isOnCooldown(playerId, sellCooldowns, SELL_COOLDOWN_HOURS)) {
            return formatCooldownMessage(player.getName(), sellCooldowns.get(playerId), SELL_COOLDOWN_HOURS);
        }

        sellCooldowns.put(playerId, LocalDateTime.now());

        if (player.getValue() < 100) {
            return player.getName() + ", у тебя слишком мало мороженого для продажи.\nКлиент ушел недовольный.";
        }

        return processSale(player);
    }

    public String makeTop() {
        StringBuilder sb = new StringBuilder();
        sb.append("Лучшие мороженщики Tutti Frutti:\n");

        playerController.makeTopPlayers().forEach(player ->
                sb.append("\n").append(player.getName()).append(": ").append(player.getProfit()).append(" руб."));

        return sb.toString();
    }

    private Player getOrCreatePlayer(Long playerId, String playerName) {
        Player player = playerController.findPlayer(playerId);
        if (player == null) {
            player = new Player(playerId, playerName, 0, 0);
        }
        return player;
    }

    private boolean isOnCooldown(Long playerId, Map<Long, LocalDateTime> cooldownMap, int hours) {
        return cooldownMap.containsKey(playerId) &&
                LocalDateTime.now().isBefore(cooldownMap.get(playerId).plusHours(hours));
    }

    private String formatCooldownMessage(String playerName, LocalDateTime lastUsage, int cooldownHours) {
        long remainingTime = getRemainingTime(lastUsage, cooldownHours);
        return playerName + ", слишком большая нагрузка поставщиков.\n" +
                "Следующая поставка доступна через " + (remainingTime) + " мин.";
    }

    private long getRemainingTime(LocalDateTime lastUsage, int cooldownHours) {
        return Duration.between(LocalDateTime.now(), lastUsage.plusHours(cooldownHours)).toMinutes();
    }

    private int generateRandomValue(int min, int max, double positiveProbability) {
        if (ThreadLocalRandom.current().nextDouble() < positiveProbability) {
            return ThreadLocalRandom.current().nextInt(0, max + 1);
        } else {
            return ThreadLocalRandom.current().nextInt(min, 0);
        }
    }

    private String formatResultMessage(String playerName, int value, int newValue) {
        return playerName + ", " +
                (value < 0 ? "куда-то пропало " + Math.abs(value) + " гр. мороженого при инвентаризации.\n" :
                        "тебе привезли " + value + " гр. мороженого.\n") +
                "Теперь у тебя " + newValue + " гр. мороженого на точке.";
    }

    private String processSale(Player player) {
        int min = 100;
        int max = player.getValue();
        int value = ThreadLocalRandom.current().nextInt(min, max + 1);

        if (ThreadLocalRandom.current().nextDouble() < SELL_POSITIVE_PROBABILITY) {
            return processSuccessfulSale(player, value);
        } else {
            return processFailedSale(player, value);
        }
    }

    private String processSuccessfulSale(Player player, int value) {
        player.setValue(player.getValue() - value);
        player.setProfit(player.getProfit() + value * 3);
        playerController.savePlayer(player);

        return player.getName() + ", ты продал " + value + " гр. мороженого и получил за это " + value * 3 + " руб.";
    }

    private String processFailedSale(Player player, int value) {
        player.setValue(player.getValue() - value);
        player.setProfit(player.getProfit() - value * 3);
        playerController.savePlayer(player);

        return player.getName() + ", ты засмотрелся на жопу студентки, и у тебя спиздили " + value + " гр. мороженого.\n" +
                "Тебе пришлось заплатить " + value * 3 + " руб.";
    }
}
