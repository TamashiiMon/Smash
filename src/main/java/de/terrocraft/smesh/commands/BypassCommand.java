package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.services.ChatService;
import de.terrocraft.smesh.services.MachMakeService;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BypassCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(new ChatService(Smash.getInstance()).noPlayer);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("smash.bypass")) {
            player.sendMessage(new ChatService(Smash.getInstance()).permission);
            return true;
        }

        if (MachMakeService.bypassPlayers.contains(player)) {
            MachMakeService.bypassPlayers.remove(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.sendMessage(new ChatService(Smash.getInstance()).prefix + "#df3f2dDu bist nun aus den Bypass modus.");
        } else {
            MachMakeService.bypassPlayers.add(player);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.sendMessage(new ChatService(Smash.getInstance()).prefix + "#df3f2dDu bist nun im Bypass modus.");
            MachMakeService.EndGameEvent(player);
        }

        return false;
    }
}
