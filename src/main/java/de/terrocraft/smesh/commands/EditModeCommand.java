package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.services.WorldService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        WorldService worldService = new WorldService(Smash.getInstance().getDataFolder());
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equals("save")) {
                worldService.saveMap(player);
            } else {
                worldService.editmapmode(player, args[0]);
            }
        }
        return false;
    }
}
