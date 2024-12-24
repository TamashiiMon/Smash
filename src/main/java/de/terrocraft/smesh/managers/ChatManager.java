package de.terrocraft.smesh.managers;

import de.terrocraft.smesh.Smash;
import org.bukkit.ChatColor;

public class ChatManager {

    public String permission = format(Smash.config.getString("noperm"));
    public String prefix = format(Smash.config.getString("prefix"));
    public String noplayer = format(ChatColor.RED + "You must be a player to use this command!");
    private Smash smash;

    public ChatManager(Smash main) {
        this.smash = main;
    }

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
