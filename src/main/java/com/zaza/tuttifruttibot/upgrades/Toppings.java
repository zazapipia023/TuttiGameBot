package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum Toppings {

    PEANUT_TOPPING("PEANUT_TOPPING", "Арахис"),
    SNICKERS_TOPPING("SNICKERS_TOPPING", "Сникерс"),
    MARSHMALLOW_TOPPING("MARSHMALLOW_TOPPING", "Маршмеллоу"),
    MMS_TOPPING("MMS_TOPPING", "M&M'S"),
    MUESLI_TOPPING("MUESLI_TOPPING", "Мюсли"),
    COCONUT_TOPPING("COCONUT_TOPPING", "Кокосовая стружка"),
    CHOCOLATE_TOPPING("CHOCOLATE_TOPPING", "Шоколадная стружка"),
    JELLY_TOPPING("JELLY_TOPPING", "Мармелад");


    private final String topping;
    private final String toppingRU;

    Toppings(String topping, String toppingRU) {
        this.topping = topping;
        this.toppingRU = toppingRU;
    }
}
