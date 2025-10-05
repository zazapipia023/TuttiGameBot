package com.zaza.tuttifruttibot.utils;

import lombok.Getter;

@Getter
public enum TelegramEmoji {

    SHAVED_ICE("\uD83C\uDF67"),
    ICE_CREAM("\uD83C\uDF68"),
    BOWL_SPOON("\uD83E\uDD63"),
    ICE_CREAM_WAFFLE("\uD83C\uDF66"),
    CHAIR("\uD83E\uDE91");


    private final String emojiCode;

    TelegramEmoji(String emojiCode) {
        this.emojiCode = emojiCode;
    }
}
