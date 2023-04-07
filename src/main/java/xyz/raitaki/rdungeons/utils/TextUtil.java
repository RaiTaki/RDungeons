package xyz.raitaki.rdungeons.utils;

import net.md_5.bungee.api.ChatColor;

public class TextUtil {

    public static String getColored(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
