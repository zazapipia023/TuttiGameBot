package com.zaza.tuttifruttibot.bot;

import com.zaza.tuttifruttibot.config.BotConfig;
import com.zaza.tuttifruttibot.config.BotContext;
import com.zaza.tuttifruttibot.game.TuttiShopService;
import com.zaza.tuttifruttibot.game.TuttiFruttiService;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import com.zaza.tuttifruttibot.upgrades.HardwareEquipment;
import com.zaza.tuttifruttibot.upgrades.IceCreamTypes;
import com.zaza.tuttifruttibot.upgrades.Toppings;
import com.zaza.tuttifruttibot.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final TelegramSender telegramSender;
    private final TuttiFruttiService tuttiService;
    private final TuttiShopService tuttiShopService;
    private final BotContext botContext;


    @Override
    public String getBotUsername() {
        log.debug("Getting bot username: {}", botConfig.getBotName());
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        log.trace("Getting bot token");
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            log.info("Callback Query received: {} from {}", update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom().getId());
            processCallbackQuery(update);
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.debug("Received update without message or text: {}", update);
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        String userName = update.getMessage().getFrom().getUserName();
        String firstName = update.getMessage().getFrom().getFirstName();

        log.info("Received message from {} ({}): {} in chat {}",
                userName, firstName, messageText, chatId);

        if (update.getMessage().isSuperGroupMessage()) {
            log.debug("Message is from supergroup: {}", chatId);
            processGroupMessage(update, chatId, messageText);
        } else {
            log.debug("Message is not from supergroup, ignoring: {}", chatId);
        }

    }

    private void processCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        log.info("callback data: {}, chatId: {}, messageId: {}, userId: {}", callbackData, chatId, messageId, userId);
        try {
            if (!botContext.getData(userId).equals(messageId)) {
                telegramSender.sendNotYourMessageResponse(update.getCallbackQuery().getId());
                return;
            }
        } catch (NullPointerException e) {
            telegramSender.sendNotYourMessageResponse(update.getCallbackQuery().getId());
            return;
        }

        if (Toppings.isTopping(callbackData)) {
            tuttiShopService.processToppingUpgrade(callbackData, chatId, userId, update.getCallbackQuery().getId());
            return;
        }
        if (HardwareEquipment.isHardwareEquipment(callbackData)) {
            tuttiShopService.processHardwareUpgrade(callbackData, chatId, userId, update.getCallbackQuery().getId());
            return;
        }
        if (IceCreamTypes.isIceCreamType(callbackData)) {
            tuttiShopService.processIceCreamUpgrade(callbackData, chatId, userId, update.getCallbackQuery().getId());
            return;
        }


        switch (callbackData) {
            case "create_shop" -> {
                String text = "Чтобы открыть новую точку, нужно 500.000 рублей.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
            case "upgrade_shop" -> {
                String text = "Выбери, что ты хочешь добавить на свою точку";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
            case "statistics_shop" -> {
                String text = "Статистика твоей точки:\n";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
            case "actions_shop" -> {
                String text = "Выбери доступное действие:";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
            case "back" -> {
                String text = "Выбери доступное действие:";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createInlineKeyboard());
            }
            case "open_shop" -> {
                boolean isShopOpened = tuttiShopService.processShopBuying(userId);
                String text;
                if (isShopOpened) {
                    text = "Вы успешно открыли новую точку, поздравляем!";
                } else {
                    text = "У вас недостаточно денег для открытия точки.";
                }
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createEmptyKeyboardMarkup());
            }
            case "toppings" -> {
                String text = "Стоимость топпинга: 15.000 рублей.\n\nВыбери, какой хочешь купить на точку.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
            case "ice_cream" -> {
                String text = "Стоимость нового вкуса: 65.000 рублей.\n\nВыбери, какой хочешь купить на точку.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
            case "hardware" -> {
                String text = "Стоимость фурнитуры: 150.000 рублей.\n\nВыбери, что хочешь поставить на точку.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createNewKeyboard(callbackData));
            }
        }
    }

    private void processGroupMessage(Update update, Long chatId, String messageText) {
        try {
            switch (messageText) {
                case "/tutti_frutti@idrakG_bot", "/tutti_frutti@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti command from chat {}", chatId);
                    String response = tuttiService.makeIceCream(update);
                    telegramSender.sendDeletingMessage(chatId, response, update.getMessage().getMessageId());
                    log.debug("Successfully processed /tutti_frutti command");
                }

                case "/tutti_frutti_top@idrakG_bot", "/tutti_frutti_top@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_top command from chat {}", chatId);
                    String response = tuttiService.makeTop();
                    telegramSender.sendMessage(chatId, response);
                    log.debug("Successfully processed /tutti_frutti_top command");
                }

                case "/tutti_frutti_sell@idrakG_bot", "/tutti_frutti_sell@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_sell command from chat {}", chatId);
                    String response = tuttiService.sellIceCream(update);
                    telegramSender.sendMessage(chatId, response);
                    log.debug("Successfully processed /tutti_frutti_sell command");
                }

                case "/tutti_frutti_check@idrakG_bot", "/tutti_frutti_check@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_check command from chat {}", chatId);
                    String response = tuttiService.getIceCreamValue(update);
                    telegramSender.sendDeletingMessage(chatId, response, update.getMessage().getMessageId());
                    log.debug("Successfully processed /tutti_frutti_check command");
                }

                case "/tutti_frutti_shop@idrakG_bot", "/tutti_frutti_shop@colizeum_csa_bot" -> {
                    log.info("Processing /tutti_frutti_shop command from chat {}", chatId);
                    if (botContext.getData(update.getMessage().getFrom().getId()) != null) {
                        telegramSender.deleteMessage(chatId, botContext.getData(update.getMessage().getFrom().getId()));
                    }

                    String response = tuttiShopService.getShopsData(update);
                    telegramSender.sendMessage(chatId, response, update.getMessage().getFrom().getId(), KeyboardUtils.createInlineKeyboard());
                    log.debug("Successfully processed /tutti_frutti_shop command");
                }

                default -> {
                    log.debug("Unknown command received: {}", messageText);
                }
            }
        } catch (Exception e) {
            log.error("Error processing command: {} in chat {}", messageText, chatId, e);
            handleProcessingError(chatId, e, update.getMessage().getMessageId());
        }
    }

    private void handleProcessingError(Long chatId, Exception e, Integer userMessageId) {
        try {
            String errorMessage = "Произошла ошибка при обработке команды. Попробуйте позже.";
            telegramSender.sendMessage(chatId, errorMessage);
            log.warn("Sent error message to chat {}", chatId);
        } catch (Exception ex) {
            log.error("Failed to send error message to chat {}", chatId, ex);
        }
    }
}
