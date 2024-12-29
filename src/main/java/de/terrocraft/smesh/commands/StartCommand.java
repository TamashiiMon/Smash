package de.terrocraft.smesh.commands;

import de.terrocraft.smesh.Gamestates;
import de.terrocraft.smesh.Smash;
import de.terrocraft.smesh.countdowns.PreGameTimer;
import de.terrocraft.smesh.managers.ChatManager;
import de.terrocraft.smesh.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartCommand implements CommandExecutor, TabCompleter {

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
        }
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (args.length == 0) {
                p.sendMessage(new ChatManager(smash).prefix + "Usage: /start <world-name>");
                return true;
            }
            if (WorldManager.iseditmodeactive) {
                p.sendMessage(new ChatManager(smash).prefix + "Der edit mode ist aktiviert, du kanst kein spiel starten.");
                return false;
            }
            if (args.length == 1) {
                if (args[0].equals("random")){
                    Random rand = new Random();
                    WorldManager worldManager = new WorldManager(Smash.getInstance().getDataFolder());
                    String randomMapName =  worldManager.getMapNames().get(rand.nextInt(worldManager.getMapNames().size()));
                    if (!new WorldManager(Smash.getInstance().getDataFolder()).doesMapExist(randomMapName)) {
                        p.sendMessage(ChatColor.DARK_PURPLE + "Fehler! Random world system broken!");
                        return true;
                    }
                    new PreGameTimer(smash, randomMapName).startCountdown();
                }
                if (!new WorldManager(Smash.getInstance().getDataFolder()).doesMapExist(args[0])) {
                    p.sendMessage(ChatColor.DARK_PURPLE + "World '" + args[0] + "' does not exist.");
                    return true;
                }

                if (Smash.getInstance().getGamestate().equals(Gamestates.INGAME) || Smash.getInstance().getGamestate().equals(Gamestates.PREGAME) || Smash.getInstance().getGamestate().equals(Gamestates.ENDGAME) || Smash.getInstance().getGamestate().equals(Gamestates.Countdown)) {
                    p.sendMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dEin spiel ist noch am laufen!"));
                    return true;
                }
                if (!(Bukkit.getOnlinePlayers().size() > 1)) {
                    p.sendMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dEs müssen mindestens #e061532 #df3f2dpersonen online sein!"));
                    return true;
                }
                new PreGameTimer(smash, args[0]).startCountdown();
                p.sendMessage(new ChatManager(smash).prefix + ChatManager.hex("#df3f2dYou have started the game."));
                return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 1) {
            WorldManager worldManager = new WorldManager(Smash.getInstance().getDataFolder());
            List<String> mapnames = worldManager.getMapNames();
            mapnames.addFirst("random");
            return mapnames;
        }

        return List.of();
    }
}