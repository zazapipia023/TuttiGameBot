package com.zaza.tuttifruttibot.utils;

public class MarkdownEscaper {

    public static String escape(String text) {
        return text.replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("−", "\\−")
                .replace("–", "\\–")
                .replace("—", "\\—")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

}
