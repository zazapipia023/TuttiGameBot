package com.zaza.cleanerexceptionsbot.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class DefaultGames {

    public static final List<String> egsGames = Arrays.asList
            ("DirectXRedist", "Launcher", "Fortnite", "GenshinImpact");

    public static final List<String> vkGames = Arrays.asList
            ("Warface", "VKPlayCloud", "Distrib");

    public static final List<String> battleNetGames = Arrays.asList
            ("Hearthstone", "Overwatch");

    public static final Map<String, String> steamGames = Map.of
                    ("Apex Legends", "1172470", "Counter-Strike Global Offensive", "730",
                    "dota 2 beta", "570", "PUBG", "578080", "War Thunder", "236390",
                    "Steamworks Shared", "NOID", "Overprime", "228980");

}