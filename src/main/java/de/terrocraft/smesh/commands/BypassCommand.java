package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.MachMakeManager;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BypassCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(new ChatManager(Smash.getInstance()).noplayer);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("smash.bypass")) {
            player.sendMessage(new ChatManager(Smash.getInstance()).permission);
            return true;
        }

        if (MachMakeManager.BypassPlayers.contains(player)) {
            MachMakeManager.BypassPlayers.remove(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.sendMessage(new ChatManager(Smash.getInstance()).prefix + "§6Du bist nun aus den Bypass modus.");
        } else {
            MachMakeManager.BypassPlayers.add(player);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.sendMessage(new ChatManager(Smash.getInstance()).prefix + "§6Du bist nun im Bypass modus.");
            MachMakeManager.EndGameEvent(player);
        }

        return false;
    }
}
