package com.zaza.tuttifruttibot.bot;

import com.zaza.tuttifruttibot.config.BotContext;
import com.zaza.tuttifruttibot.game.TuttiFruttiService;
import com.zaza.tuttifruttibot.game.TuttiShopService;
import com.zaza.tuttifruttibot.sender.TelegramSender;
import com.zaza.tuttifruttibot.upgrades.HardwareEquipment;
import com.zaza.tuttifruttibot.upgrades.IceCreamTypes;
import com.zaza.tuttifruttibot.upgrades.Toppings;
import com.zaza.tuttifruttibot.utils.KeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackHandler {

    private final TelegramSender telegramSender;
    private final TuttiFruttiService tuttiFruttiService;
    private final TuttiShopService tuttiShopService;
    private final BotContext botContext;

    public void processCallback(Update update) {
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
            case "cream_income" -> {
                String text = tuttiFruttiService.makeIceCream(update);
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createBackGameKeyboard());
            }
            case "cream_sell" -> {
                String text = tuttiFruttiService.sellIceCream(update);
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createBackGameKeyboard());
            }
            case "create_shop" -> {
                String text = "Чтобы открыть новую точку, нужно 500.000 рублей.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createProcessShopKeyboard(callbackData));
            }
            case "upgrade_shop" -> {
                String text = "Выбери, что ты хочешь добавить на свою точку";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createProcessShopKeyboard(callbackData));
            }
            case "statistics_shop" -> {
                String text = tuttiShopService.getShopStats(userId);
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createBackKeyboardMarkup());
            }
            case "actions_shop" -> {
                String text = "Выбери доступное действие:";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createProcessShopKeyboard(callbackData));
            }
            case "back" -> {
                String text = "Выбери доступное действие:";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createShopKeyboard());
            }
            case "back_game" -> {
                String text = "Выбери доступное действие:";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createGameKeyboard());
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
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createProcessShopKeyboard(callbackData));
            }
            case "ice_cream" -> {
                String text = "Стоимость нового вкуса: 65.000 рублей.\n\nВыбери, какой хочешь купить на точку.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createProcessShopKeyboard(callbackData));
            }
            case "hardware" -> {
                String text = "Стоимость фурнитуры: 150.000 рублей.\n\nВыбери, что хочешь поставить на точку.";
                telegramSender.editMessageWithMarkup(chatId, messageId, text, KeyboardUtils.createProcessShopKeyboard(callbackData));
            }
            case "take_profit" -> {
                tuttiShopService.processEncashment(chatId, messageId, userId);
            }
        }
    }

}
