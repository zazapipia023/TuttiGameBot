package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum HardwareEquipment {

    CASH_REGISTER("CASH_REGISTER", "Кассовый аппарат"),
    CREAM_DISPENSER("CREAM_DISPENSER", "Диспенсер мороженого"),
    STAND_FOR_INGREDIENTS("INGREDIENTS_STAND", "Стойка для ингредиентов"),
    STAND_FOR_SPOONS_AND_NAPKINS("SPOONS_NAPKINS_STAND", "Стэнд для ложек и салфеток"),
    TABLES("TABLES", "Столы для посетителей");

    private final String equipment;
    private final String equipmentRU;

    HardwareEquipment(String equipment, String equipmentRU) {
        this.equipment = equipment;
        this.equipmentRU = equipmentRU;
    }
}
