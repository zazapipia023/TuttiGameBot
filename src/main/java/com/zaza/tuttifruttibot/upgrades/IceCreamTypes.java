package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum IceCreamTypes {

    CHOCOLATE_CREAM("CHOCOLATE_CREAM", "Шоколадное"),
    BANANA_CREAM("BANANA_CREAM", "Банановое"),
    VANILLA_CREAM("VANILLA_CREAM", "Ванильное"),
    CARAMEL_CREAM("CARAMEL_CREAM", "Карамельное");

    private final String type;
    private final String typeRU;

    IceCreamTypes(String type, String typeRU) {
        this.type = type;
        this.typeRU = typeRU;
    }
}
