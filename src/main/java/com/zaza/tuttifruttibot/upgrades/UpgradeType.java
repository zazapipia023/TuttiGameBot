package com.zaza.tuttifruttibot.upgrades;

import lombok.Getter;

@Getter
public enum UpgradeType {

    TOPPING("топпинг"),
    ICE_CREAM("вкус мороженого"),
    HARDWARE("фурнитура");

    private final String russianName;

    UpgradeType(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}
