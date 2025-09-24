package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum Toppings {

    PEANUT_TOPPING("PEANUT_TOPPING", "Арахис", 1.05),
    SNICKERS_TOPPING("SNICKERS_TOPPING", "Сникерс", 1.05),
    MARSHMALLOW_TOPPING("MARSHMALLOW_TOPPING", "Маршмеллоу", 1.05),
    MMS_TOPPING("MMS_TOPPING", "M&M'S", 1.05),
    MUESLI_TOPPING("MUESLI_TOPPING", "Мюсли", 1.05),
    COCONUT_TOPPING("COCONUT_TOPPING", "Кокосовая стружка", 1.05),
    CHOCOLATE_TOPPING("CHOCOLATE_TOPPING", "Шоколадная стружка", 1.05),
    JELLY_TOPPING("JELLY_TOPPING", "Мармелад", 1.05);


    private final String topping;
    private final String toppingRU;
    private final double index;

    Toppings(String topping, String toppingRU, double index) {
        this.topping = topping;
        this.toppingRU = toppingRU;
        this.index = index;
    }

    public static boolean isTopping(String topping) {
        for (Toppings t : Toppings.values()) {
            if (t.topping.equals(topping)) {
                return true;
            }
        }
        return false;
    }

    public static String getTranslate(String topping) {
        return Toppings.valueOf(topping).getToppingRU();
    }

    public static double getIndex(String topping) {
        return Toppings.valueOf(topping).getIndex();
    }

}
