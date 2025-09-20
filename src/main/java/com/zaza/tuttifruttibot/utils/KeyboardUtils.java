package com.zaza.tuttifruttibot.utils;

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

    public static InlineKeyboardMarkup createInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createInlineButton("Открыть точку", "create_shop"));
        row1.add(createInlineButton("In Progress", "upgrade_shop1"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createInlineButton("In Progress", "statistics_shop1"));
        row2.add(createInlineButton("In Progress", "actions_shop1"));

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
                row1.add(createInlineButton("Отправить мороженое", "send_cream"));
                row1.add(createInlineButton("Перевести деньги", "send_profit"));

                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(createInlineButton("Назад", "back"));

                rows.add(row1);
                rows.add(row2);
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
