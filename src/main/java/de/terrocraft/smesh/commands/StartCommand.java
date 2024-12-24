package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.countdowns.PreGameTimer;
import de.terrocraft.smesh.managers.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    private Smash smash;

    public StartCommand(Smash main) {
        this.smash = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;

        if (!p.hasPermission("smash.start")) {
            p.sendMessage(new ChatManager(smash).permission);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("start")) {
            if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME) || Smash.getInstance().getGamestate().equals(Gamestates.PREGAME)) {
                p.sendMessage(new ChatManager(smash).prefix + "&4Ein spiel ist noch am laufen!");
                return true;
            }
            if (!(Bukkit.getOnlinePlayers().size() > 1)){
                p.sendMessage(new ChatManager(smash) + "&4Es müssen mindestens 2 personen online sein!");
                return true;
            }
            new PreGameTimer(smash).startCountdown();
            p.sendMessage(new ChatManager(smash).prefix + "You have started the game.");
            return true;
        }
        return true;
    }
}