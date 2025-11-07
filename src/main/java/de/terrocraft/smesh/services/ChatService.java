package de.terrocraft.smesh.services;

import de.terrocraft.smesh.Smash;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatService {

    public String prefix = format(Smash.config.getString("prefix"));
    public String permission = format(prefix + hex("#df3f2dDu hast keine rechte um dies zu tuhen!"));
    public String noPlayer = format(prefix + hex("#df3f2dYou must be a player to use this command!"));
    private final Smash smash;

    public ChatService(Smash main) {
        this.smash = main;
    }

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group();
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            matcher.appendReplacement(result, builder.toString());
        }

        matcher.appendTail(result);
        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }

}