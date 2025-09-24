package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum HardwareEquipment {

    CASH_REGISTER("CASH_REGISTER", "Кассовый аппарат", 1.1),
    CREAM_DISPENSER("CREAM_DISPENSER", "Диспенсер мороженого", 1.5),
    STAND_FOR_INGREDIENTS("INGREDIENTS_STAND", "Стойка для ингредиентов", 1.7),
    STAND_FOR_SPOONS_AND_NAPKINS("SPOONS_NAPKINS_STAND", "Стэнд для ложек и салфеток", 1.05),
    TABLES("TABLES", "Столы для посетителей", 1.05);

    private final String equipment;
    private final String equipmentRU;
    private final double index;

    HardwareEquipment(String equipment, String equipmentRU, double index) {
        this.equipment = equipment;
        this.equipmentRU = equipmentRU;
        this.index = index;
    }

    public static boolean isHardwareEquipment(String equipment) {
        for (HardwareEquipment e : HardwareEquipment.values()) {
            if (e.equipment.equals(equipment)) {
                return true;
            }
        }
        return false;
    }

    public static String getTranslate(String hardware) {
        return HardwareEquipment.valueOf(hardware).getEquipmentRU();
    }

    public static double getIndex(String hardware) {
        return HardwareEquipment.valueOf(hardware).getIndex();
    }
}
