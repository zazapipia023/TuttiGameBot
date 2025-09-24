package com.zaza.tuttifruttibot.utils;

import com.zaza.tuttifruttibot.upgrades.HardwareEquipment;
import com.zaza.tuttifruttibot.upgrades.IceCreamTypes;
import com.zaza.tuttifruttibot.upgrades.Toppings;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtils {

    public static InlineKeyboardMarkup createEmptyKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        inlineKeyboard.setKeyboard(rows);

        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup createBackKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createInlineButton("Назад", "back"));

        rows.add(row1);

        inlineKeyboard.setKeyboard(rows);
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup createInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createInlineButton("Открыть точку", "create_shop"));
        row1.add(createInlineButton("Улучшения", "upgrade_shop"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createInlineButton("In Progress", "statistics_shop1"));
        row2.add(createInlineButton("Действия", "actions_shop"));

        rows.add(row1);
        rows.add(row2);

        inlineKeyboard.setKeyboard(rows);
        return inlineKeyboard;
    }

    public static InlineKeyboardMarkup createNewKeyboard(String selectedOption) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        switch (selectedOption) {
            case "create_shop" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton("Открыть точку", "open_shop"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton("Назад", "back"));

                rows.add(row1);
                rows.add(row2);
            }
            case "upgrade_shop" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton("Топпинги", "toppings"));
                row1.add(createInlineButton("Мороженое", "ice_cream"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton("Техника и интерьер", "hardware"));

                List<InlineKeyboardButton> row3 = new ArrayList<>();
                row3.add(createInlineButton("Назад", "back"));

                rows.add(row1);
                rows.add(row2);
                rows.add(row3);
            }
            case "statistics_shop" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton("Назад", "back"));

                rows.add(row1);
            }
            case "actions_shop" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton("Инкассация", "take_profit"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton("Назад", "back"));

                rows.add(row1);
                rows.add(row2);
            }
            case "toppings" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton(Toppings.CHOCOLATE_TOPPING.getToppingRU(), "CHOCOLATE_TOPPING"));
                row1.add(createInlineButton(Toppings.JELLY_TOPPING.getToppingRU(), "JELLY_TOPPING"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton(Toppings.COCONUT_TOPPING.getToppingRU(), "COCONUT_TOPPING"));
                row2.add(createInlineButton(Toppings.MUESLI_TOPPING.getToppingRU(), "MUESLI_TOPPING"));

                List<InlineKeyboardButton> row3 = new ArrayList<>();
                row3.add(createInlineButton(Toppings.MMS_TOPPING.getToppingRU(), "MMS_TOPPING"));
                row3.add(createInlineButton(Toppings.MARSHMALLOW_TOPPING.getToppingRU(), "MARSHMALLOW_TOPPING"));

                List<InlineKeyboardButton> row4 = new ArrayList<>();
                row4.add(createInlineButton(Toppings.SNICKERS_TOPPING.getToppingRU(), "SNICKERS_TOPPING"));
                row4.add(createInlineButton(Toppings.PEANUT_TOPPING.getToppingRU(), "PEANUT_TOPPING"));

                List<InlineKeyboardButton> row5 = new ArrayList<>();
                row5.add(createInlineButton("Назад",  "back"));

                rows.add(row1);
                rows.add(row2);
                rows.add(row3);
                rows.add(row4);
                rows.add(row5);
            }
            case "ice_cream" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton(IceCreamTypes.CHOCOLATE_CREAM.getTypeRU(), "CHOCOLATE_CREAM"));
                row1.add(createInlineButton(IceCreamTypes.BANANA_CREAM.getTypeRU(), "BANANA_CREAM"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton(IceCreamTypes.VANILLA_CREAM.getTypeRU(), "VANILLA_CREAM"));
                row2.add(createInlineButton(IceCreamTypes.CARAMEL_CREAM.getTypeRU(), "CARAMEL_CREAM"));

                List<InlineKeyboardButton> row3 = new ArrayList<>();
                row3.add(createInlineButton("Назад",  "back"));

                rows.add(row1);
                rows.add(row2);
                rows.add(row3);
            }
            case "hardware" -> {
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(createInlineButton(HardwareEquipment.CASH_REGISTER.getEquipmentRU(),  "CASH_REGISTER"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton(HardwareEquipment.CREAM_DISPENSER.getEquipmentRU(),  "CREAM_DISPENSER"));

                List<InlineKeyboardButton> row3 = new ArrayList<>();
                row3.add(createInlineButton(HardwareEquipment.STAND_FOR_INGREDIENTS.getEquipmentRU(),  "INGREDIENTS_STAND"));

                List<InlineKeyboardButton> row4 = new ArrayList<>();
                row4.add(createInlineButton(HardwareEquipment.STAND_FOR_SPOONS_AND_NAPKINS.getEquipmentRU(),  "SPOONS_NAPKINS_STAND"));

                List<InlineKeyboardButton> row5 = new ArrayList<>();
                row5.add(createInlineButton(HardwareEquipment.TABLES.getEquipmentRU(),  "TABLES"));

                List<InlineKeyboardButton> row6 = new ArrayList<>();
                row6.add(createInlineButton("Назад",  "back"));

                rows.add(row1);
                rows.add(row2);
                rows.add(row3);
                rows.add(row4);
                rows.add(row5);
                rows.add(row6);
            }
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private static InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

}
