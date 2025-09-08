package com.zaza.cleanerexceptionsbot.bot;

import com.zaza.cleanerexceptionsbot.config.BotConfig;
import com.zaza.cleanerexceptionsbot.controllers.PlayerController;
import com.zaza.cleanerexceptionsbot.models.Player;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final PlayerController playerController;
    private final TelegramSender telegramSender;
    private final Map<Long, LocalDateTime> lastCommandUsage = new ConcurrentHashMap<>();
    private static final int COOLDOWN_HOURS = 5;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.getMessage().isSuperGroupMessage()) {
            if (update.getMessage().getText().equals(/*"/tutti_frutti@idrakG_bot"*/ "/tutti_frutti@colizeum_csa_bot")) {
                makeIceCream(update);
            }
            if (update.getMessage().getText().equals(/*"/tutti_frutti_top@idrakG_bot"*/ "/tutti_frutti_top@colizeum_csa_bot")) {
                makeTop(update);
            }
        }

    }

    private void makeIceCream(Update update) {
        Long playerId = update.getMessage().getFrom().getId();
        String playerName = update.getMessage().getFrom().getFirstName();

        if (isOnCooldown(playerId)) {
            LocalDateTime lastUsage = lastCommandUsage.get(playerId);
            LocalDateTime nextAvailable = lastUsage.plusHours(COOLDOWN_HOURS);

            telegramSender.sendMessage(update.getMessage().getChatId(),
                    playerName + ", слишком большая нагрузка поставщиков.\n" +
                            "Следующая поставка доступна через " + (getRemainingTime(lastUsage) + 1) + " час(ов)");
            return;
        }

        lastCommandUsage.put(playerId, LocalDateTime.now());

        Player player = playerController.findPlayer(playerId);

        if (player == null) {
            player = new Player(playerId, playerName, 0);
        }

        int min = -500;
        int max = 1000;
        double pPositive = 0.8;
        int value;
        if (ThreadLocalRandom.current().nextDouble() < pPositive) {
            value = ThreadLocalRandom.current().nextInt(0, max + 1);
        } else {
            value = ThreadLocalRandom.current().nextInt(min, 0);
        }

        int newValue = player.getValue() + value;
        player.setValue(newValue);
        playerController.savePlayer(player);

        telegramSender.sendMessage(update.getMessage().getChatId(),
                playerName + ", " +
                        (value < 0 ? "куда-то пропало " + Math.abs(value) + " грамм мороженного при инвентаризации.\n" :
                                "тебе привезли " + value + " грамм мороженного.\n" ) +
                "Теперь у тебя " + newValue + " мороженного на точке.");
    }

    private boolean isOnCooldown(Long playerId) {
        if (!lastCommandUsage.containsKey(playerId)) {
            return false;
        }

        LocalDateTime lastUsage = lastCommandUsage.get(playerId);
        return LocalDateTime.now().isBefore(lastUsage.plusHours(COOLDOWN_HOURS));
    }

    private long getRemainingTime(LocalDateTime lastUsage) {
        LocalDateTime nextAvailable = lastUsage.plusHours(COOLDOWN_HOURS);
        java.time.Duration duration = java.time.Duration.between(LocalDateTime.now(), nextAvailable);
        return duration.toHours();
    }

    private void makeTop(Update update) {
        StringBuilder sb = new StringBuilder();
        sb.append("Лучшие мороженщики Tutti Frutti:\n");

        playerController.makeTopPlayers().forEach(player ->
                sb.append("\n").append(player.getName()).append(": ").append(player.getValue()).append(" грамм."));

        telegramSender.sendMessage(update.getMessage().getChatId(), sb.toString());
    }
}
