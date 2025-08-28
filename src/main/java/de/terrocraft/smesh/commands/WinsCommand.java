package de.terrocraft.smesh.commands;

import com.nexomc.nexo.pack.PackListener;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.Utils.CoinManager;
import de.terrocraft.smesh.managers.ChatManager;
import net.momirealms.customnameplates.common.event.CommandFeedbackEvent;
import net.momirealms.customnameplates.common.event.NameplatesEvent;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import net.momirealms.customnameplates.libraries.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CoinManager.doesPlayerExist(player.getUniqueId()).thenAccept(exists -> {
                if (!exists) {
                    CoinManager.savePlayerData(player.getUniqueId(), 0,0);
                    player.sendMessage(new ChatManager(Smash.getInstance()).prefix + ChatManager.hex("#df3f2dDu hast #e06153§l0 #df3f2dWins!"));
                } else {
                    player.sendMessage(new ChatManager(Smash.getInstance()).prefix + ChatManager.hex("#df3f2dDu hast #e06153§l" + CoinManager.getWins(player.getUniqueId()) + " #df3f2dWins!"));
                }
            });

        } else {
            sender.sendMessage(new ChatManager(Smash.getInstance()).noplayer);
        }
        return false;
    }
}
