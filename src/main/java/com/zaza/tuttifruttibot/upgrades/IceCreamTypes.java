package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum IceCreamTypes {

    CHOCOLATE_CREAM("CHOCOLATE_CREAM", "Шоколадное", 1.1),
    BANANA_CREAM("BANANA_CREAM", "Банановое", 1.1),
    VANILLA_CREAM("VANILLA_CREAM", "Ванильное", 1.1),
    CARAMEL_CREAM("CARAMEL_CREAM", "Карамельное", 1.1);

    private final String type;
    private final String typeRU;
    private final double index;

    IceCreamTypes(String type, String typeRU, double index) {
        this.type = type;
        this.typeRU = typeRU;
        this.index = index;
    }

    public static boolean isIceCreamType(String type) {
        for (IceCreamTypes t : IceCreamTypes.values()) {
            if (t.type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static String getTranslate(String iceCream) {
        return IceCreamTypes.valueOf(iceCream).getTypeRU();
    }

    public static double getIndex(String iceCreamType) {
        return IceCreamTypes.valueOf(iceCreamType).getIndex();
    }
}
